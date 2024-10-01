package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.USER_ID;
import static iudx.apd.acl.server.authentication.model.DxRole.DELEGATE;
import static iudx.apd.acl.server.authentication.util.Constants.*;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import iudx.apd.acl.server.aaaService.AuthClient;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.authentication.model.DxRole;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.authentication.model.UserInfo;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.RoutingContextHelper;
import iudx.apd.acl.server.policy.PostgresService;
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
    UserInfo user = addUserInfo(event);
    Future<User> getUserInfoFuture = getUserInfo(user);
    getUserInfoFuture.onComplete(
        handler -> {
          if (handler.succeeded()) {
            /* set user in routing context */
            RoutingContextHelper.setUser(event, handler.result());
            event.next();
          } else {
            LOGGER.error(
                "User info fetch, access validation failed : {}", handler.cause().getMessage());
            processAuthorizationFailure(event, handler.cause().getMessage());
          }
        });
  }

  /**
   * Converts delegate user to its respective consumer or provider
   *
   * @param event
   * @return vert.x Json object containing consumer or provider info
   */
  public UserInfo addUserInfo(RoutingContext event) {
    JwtData jwtData = RoutingContextHelper.getJwtData(event);
    DxRole role = DxRole.fromRole(jwtData);
    boolean isDelegate = jwtData.getRole().equalsIgnoreCase(DELEGATE.getRole());
    UUID id = UUID.fromString(isDelegate ? jwtData.getDid() : jwtData.getSub());
    userInfo
        .setDelegate(isDelegate)
        .setRole(role)
        .setAudience(jwtData.getAud())
        .setUserId(id);
    return userInfo;
  }

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

  private void processAuthorizationFailure(RoutingContext ctx, String failureMessage) {
    ResponseUrn responseUrn = INVALID_TOKEN_URN;
    HttpStatusCode statusCode = HttpStatusCode.getByValue(401);
    if (failureMessage.equalsIgnoreCase("User information is invalid")) {
      responseUrn = ResponseUrn.INTERNAL_SERVER_ERROR;
      statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
    }
    LOGGER.error("Error : Authentication Failure");
    ctx.response()
        .putHeader(CONTENT_TYPE, APPLICATION_JSON)
        .setStatusCode(statusCode.getValue())
        .end(generateResponse(responseUrn, statusCode).toString());
  }

  private JsonObject generateResponse(ResponseUrn urn, HttpStatusCode statusCode) {
    return new JsonObject()
        .put(TYPE, urn.getUrn())
        .put(TITLE, statusCode.getDescription())
        .put(DETAIL, statusCode.getDescription());
  }

  static final class UserContainer {
    User user;
  }
}
