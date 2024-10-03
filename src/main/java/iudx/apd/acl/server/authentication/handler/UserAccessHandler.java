package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.authentication.model.DxRole.DELEGATE;
import static iudx.apd.acl.server.common.ResponseUrn.INTERNAL_SERVER_ERROR;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.aaaService.AuthClient;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.authentication.model.DxRole;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.authentication.model.UserInfo;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.RoutingContextHelper;
import iudx.apd.acl.server.policy.PostgresService;
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAccessHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = LogManager.getLogger(UserAccessHandler.class);
  private final PostgresService pgService;
  private final AuthClient authClient;
  private final UserInfo userInfo;

  public UserAccessHandler(PostgresService postgresService, AuthClient client, UserInfo userInfo) {
    authClient = client;
    pgService = postgresService;
    this.userInfo = userInfo;
  }

  /**
   * After JWT Authentication, User Access Validation is handled here
   *
   * @param event the event to handle
   */
  @Override
  public void handle(RoutingContext event) {
    Future<User> getUserInfoFuture = getUserFromAuth(event);
    getUserInfoFuture.onComplete(
        handler -> {
          if (handler.succeeded()) {
            /* set user in routing context */
            RoutingContextHelper.setUser(event, handler.result());
            event.next();
          } else {
            LOGGER.error(
                "User info fetch, access validation failed : {}", handler.cause().getMessage());
            processAuthFailure(event,handler.cause().getMessage());
          }
        });
  }


  private Future<User> getUserFromAuth(RoutingContext event){
    JwtData jwtData = RoutingContextHelper.getJwtData(event);
    DxRole role = DxRole.fromRole(jwtData);
    boolean isDelegate = jwtData.getRole().equalsIgnoreCase(DELEGATE.getRole());
    UUID id = UUID.fromString(isDelegate ? jwtData.getDid() : jwtData.getSub());
    userInfo
        .setDelegate(isDelegate)
        .setRole(role)
        .setAudience(jwtData.getAud())
        .setUserId(id);
    LOGGER.info("Getting user from Auth");
    return authClient.fetchUserInfo(userInfo);

  }
/*
  private Future<User> getUserInfo(UserInfo userInfo) {
    LOGGER.info("Getting User Info.");
    Promise<User> promise = Promise.promise();
    Tuple tuple = Tuple.of(userInfo.getUserId());
    UserAccessHandler.UserContainer userContainer = new UserAccessHandler.UserContainer();
    pgService
        .getPool()
        .withConnection(
            sqlConnection ->
                sqlConnection
                    .preparedQuery(GET_USER)
                    .execute(tuple)
                    .onFailure(
                        existingIdFailureHandler -> {
                          LOGGER.error(
                              "checkIfUserExist db fail {}",
                              existingIdFailureHandler.getLocalizedMessage());
                        }))
        .onSuccess(
            rows -> {
              if (rows != null && rows.size() > 0) {
                LOGGER.info("User found in db.");
                Row row = rows.iterator().next();
                JsonObject result = row.toJson(); // Get the single row
                JsonObject userObj = new JsonObject();
                userObj.put(USER_ID, userInfo.getUserId());
                userObj.put(USER_ROLE, userInfo.getRole());
                userObj.put(EMAIL_ID, result.getString("email_id"));
                userObj.put(FIRST_NAME, result.getString("first_name"));
                userObj.put(LAST_NAME, result.getString("last_name"));
                userObj.put(RS_SERVER_URL, userInfo.getAudience());
                //
                // userObj.put(IS_DELEGATE,jsonObject.getBoolean(IS_DELEGATE));

                User user = new User(userObj);
                promise.complete(user);
              } else {
                LOGGER.info("Getting user from Auth");
                Future<User> getUserFromAuth = authClient.fetchUserInfo(userInfo);
                Future<Void> insertIntoDb =
                    getUserFromAuth.compose(
                        userObj -> {
                          userContainer.user = userObj;
                          return insertUserIntoDb(userContainer.user);
                        });
                insertIntoDb
                    .onSuccess(
                        successHandler -> {
                          promise.complete(userContainer.user);
                        })
                    .onFailure(promise::fail);
              }
            });
    return promise.future();
  }

  private Future<Void> insertUserIntoDb(User user) {
    Promise<Void> promise = Promise.promise();
    Tuple tuple =
        Tuple.of(user.getUserId(), user.getEmailId(), user.getFirstName(), user.getLastName());

    pgService
        .getPool()
        .withConnection(
            sqlConnection ->
                sqlConnection
                    .preparedQuery(INSERT_USER_TABLE)
                    .execute(tuple)
                    .onFailure(
                        existingIdFailureHandler -> {
                          LOGGER.error(
                              "insertUserIntoDb db fail {}",
                              existingIdFailureHandler.getLocalizedMessage());
                          promise.fail(existingIdFailureHandler.getLocalizedMessage());
                        })
                    .onSuccess(
                        successHandler -> {
                          LOGGER.info("User inserted in db successfully.");
                          promise.complete();
                        }));
    return promise.future();
  }
*/

  private void processAuthFailure(RoutingContext event, String failureMessage) {
    LOGGER.error("Error : Authentication Failure : {}", failureMessage);
    if (failureMessage.equalsIgnoreCase("User information is invalid")) {
      LOGGER.error("User information is invalid");
      event.fail(new DxRuntimeException(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(), INTERNAL_SERVER_ERROR));
    }
  event.fail(new DxRuntimeException(HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN));
  }

  /*  static final class UserContainer {
    User user;
  }*/
}
