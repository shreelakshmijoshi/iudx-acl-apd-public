package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.common.ResponseUrn.INTERNAL_SERVER_ERROR;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.authentication.AuthenticationService;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.RoutingContextHelper;
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthHandler implements Handler<RoutingContext> {
   AuthenticationService authenticator;
  private final Logger LOGGER = LogManager.getLogger(AuthHandler.class);

  public AuthHandler(AuthenticationService authenticationService) {
    authenticator = authenticationService;
  }

  @Override
  public void handle(RoutingContext context) {
    JsonObject authInfo = RoutingContextHelper.getAuthInfo(context);

    checkIfTokenIsAuthenticated(authInfo)
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

  Future<JwtData> checkIfTokenIsAuthenticated(JsonObject authenticationInfo) {
    Promise<JwtData> promise = Promise.promise();
    Future<JwtData> tokenIntrospect = authenticator.tokenIntrospect(authenticationInfo);
    tokenIntrospect.onSuccess(promise::complete).onFailure(promise::fail);
    return promise.future();
  }

  private void processAuthFailure(RoutingContext context, String failureMessage) {
    LOGGER.error("Error : Authentication Failure : {}", failureMessage);
    if (failureMessage.equalsIgnoreCase("User information is invalid")) {
      LOGGER.error("User information is invalid");
      context.fail(new DxRuntimeException(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(), INTERNAL_SERVER_ERROR));
    }
    context.fail(new DxRuntimeException(HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN));
  }

}
