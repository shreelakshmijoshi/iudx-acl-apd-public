package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.DETAIL;
import static iudx.apd.acl.server.common.ResponseUrn.INTERNAL_SERVER_ERROR;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.authentication.AuthenticationService;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.RoutingContextHelper;
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VerifyAuthHandler implements Handler<RoutingContext> {
   AuthenticationService authenticator;
  private final Logger LOGGER = LogManager.getLogger(VerifyAuthHandler.class);

  public VerifyAuthHandler(AuthenticationService authenticationService) {
    authenticator = authenticationService;
  }

  @Override
  public void handle(RoutingContext context) {
    String token = RoutingContextHelper.getToken(context);
    JsonObject authInfo = RoutingContextHelper.getAuthInfo(context);

    if (token.trim().split(" ").length == 2) {
      token = token.trim().split(" ")[1];
      authInfo.put(HEADER_TOKEN, token);
      Future<Void> verifyFuture = authenticator.tokenIntrospectForVerify(authInfo);
      verifyFuture.onComplete(
          verifyHandler -> {
            if (verifyHandler.succeeded()) {
              LOGGER.info("User Verified Successfully.");
              context.next();
            } else if (verifyHandler.failed()) {
              LOGGER.error("User Verification Failed. " + verifyHandler.cause().getMessage());
              processAuthFailure(context,verifyHandler.cause().getMessage());
            }
          });
    } else {
      processAuthFailure(context,"invalid token");
    }
  }

  private void processAuthFailure(RoutingContext context, String failureMessage){
    LOGGER.error("Error : Authentication Failure : {}", failureMessage);
    if (failureMessage.equalsIgnoreCase("User information is invalid")) {
      LOGGER.error("User information is invalid");
      context.fail(new DxRuntimeException(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(), INTERNAL_SERVER_ERROR));
    }
    context.fail(new DxRuntimeException(HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN));
  }
}
