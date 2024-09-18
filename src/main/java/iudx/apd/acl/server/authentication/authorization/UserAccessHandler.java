package iudx.apd.acl.server.authentication.authorization;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.ROLE;
import static iudx.apd.acl.server.apiserver.util.Constants.USER_ID;
import static iudx.apd.acl.server.authentication.Constants.*;
import static iudx.apd.acl.server.authentication.Constants.AUD;
import static iudx.apd.acl.server.authentication.authorization.DxRole.DELEGATE;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.aaaService.AuthClient;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.common.Api;
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
  public UserAccessHandler(PostgresService postgresService, AuthClient client)
  {
    authClient = client;
    pgService = postgresService;
  }
  /**
   * After JWT Authentication, User Access Validation is handled here
   *
   * @param event the event to handle
   */
  @Override
  public void handle(RoutingContext event) {
   Future<JsonObject> validateUserAccessFuture = validateAccess(event);
    Future<User> getUserInfoFuture = validateUserAccessFuture.compose(this::getUserInfo);
    getUserInfoFuture.onComplete(handler -> {
      if(handler.succeeded())
      {
        /* set user in routing context */
        RoutingContextHelper.setUser(event, handler.result());
        event.next();
      }
      else
      {
        LOGGER.error("User info fetch, access validation failed : {}",handler.cause().getMessage());
        processAuthorizationFailure(event, handler.cause().getMessage());
      }
    });

  }
  
  public Future<JsonObject> validateAccess(RoutingContext event){
    LOGGER.info("Authorization check started");
    Promise<JsonObject> promise = Promise.promise();
    Method method = Method.valueOf(RoutingContextHelper.getMethod(event));
    String api = RoutingContextHelper.getRequestPath(event);
    AuthorizationRequest authRequest = new AuthorizationRequest(method, api);
    JwtData jwtData = RoutingContextHelper.getJwtData(event);
    DxRole role = DxRole.fromRole(jwtData);
    Api apis = RoutingContextHelper.getApis(event);
    LOGGER.info("api : {}", api.toString());

    AuthorizationStrategy authStrategy = AuthorizationContextFactory.create(role, apis);
    LOGGER.info("strategy : " + authStrategy.getClass().getSimpleName());

    JwtAuthorization jwtAuthStrategy = new JwtAuthorization(authStrategy);
    if (jwtAuthStrategy.isAuthorized(authRequest)) {
      JsonObject jsonResponse = new JsonObject();
      boolean isDelegate = jwtData.getRole().equalsIgnoreCase(DELEGATE.getRole());
      jsonResponse.put(USER_ID, isDelegate ? jwtData.getDid() : jwtData.getSub());
      jsonResponse.put(IS_DELEGATE, isDelegate);
      jsonResponse.put(ROLE, role);
      jsonResponse.put(AUD, jwtData.getAud());
      promise.complete(jsonResponse);
    } else {
      LOGGER.info("Failed in authorization check.");
      JsonObject result = new JsonObject().put("401", "no access provided to endpoint");
      promise.fail(result.toString());
    }
    return promise.future();
  }

  private Future<User> getUserInfo(JsonObject jsonObject) {
    LOGGER.info("Getting User Info.");
    Promise<User> promise = Promise.promise();
    Tuple tuple = Tuple.of(UUID.fromString(jsonObject.getString("userId")));
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
                userObj.put(USER_ID, jsonObject.getString(USER_ID));
                userObj.put(USER_ROLE, jsonObject.getString(ROLE));
                userObj.put(EMAIL_ID, result.getString("email_id"));
                userObj.put(FIRST_NAME, result.getString("first_name"));
                userObj.put(LAST_NAME, result.getString("last_name"));
                userObj.put(RS_SERVER_URL, jsonObject.getString(AUD));
                //
                // userObj.put(IS_DELEGATE,jsonObject.getBoolean(IS_DELEGATE));

                LOGGER.info("user ashadfadfhkadf : " + userObj.encodePrettily());
                User user = new User(userObj);
                promise.complete(user);
              } else {
                LOGGER.info("Getting user from Auth");
                Future<User> getUserFromAuth = authClient.fetchUserInfo(jsonObject);
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
