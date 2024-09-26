package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.authentication.AuthenticationService;
import iudx.apd.acl.server.authentication.model.DxRole;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.common.Api;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.RoutingContextHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthHandler implements Handler<RoutingContext> {
  static AuthenticationService authenticator;
  static Api api;
  private final Logger LOGGER = LogManager.getLogger(AuthHandler.class);

  public AuthHandler(
      Api apis,
      AuthenticationService authenticationService) {
    authenticator = authenticationService;
    api = apis;
  }



  @Override
  public void handle(RoutingContext context) {
    JsonObject authInfo = RoutingContextHelper.getAuthInfo(context);
    checkIfAuth(authInfo)
        .onSuccess(
            jwtData -> {
              LOGGER.info("User Verified Successfully.");
              RoutingContextHelper.setJwtData(context, jwtData);
              context.next();
            })
        .onFailure(
            fail -> {
              LOGGER.error("User Verification Failed. " + fail.getMessage());
              processAuthFailure(context, fail.getMessage());
            });
  }

  Future<JwtData> checkIfAuth(JsonObject authenticationInfo) {
    Promise<JwtData> promise = Promise.promise();
    Future<JwtData> tokenIntrospect = authenticator.tokenIntrospect(authenticationInfo);

    //    Future<User> getUserInfo = tokenIntrospect.compose(this::getUserInfo);
    tokenIntrospect.onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  private void processAuthFailure(RoutingContext ctx, String failureMessage) {
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
}
