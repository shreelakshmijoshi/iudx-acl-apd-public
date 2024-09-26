package iudx.apd.acl.server.authentication.handler;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.DETAIL;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.authentication.AuthenticationService;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.RoutingContextHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VerifyAuthHandler implements Handler<RoutingContext> {
  static AuthenticationService authenticator;
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
              processAuthFailure(context, verifyHandler.cause().getMessage());
            }
          });
    } else {
      processAuthFailure(context, "invalid token");
    }
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
