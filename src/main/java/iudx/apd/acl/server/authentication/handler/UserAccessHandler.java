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
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAccessHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = LogManager.getLogger(UserAccessHandler.class);
  private final AuthClient authClient;
  private final UserInfo userInfo;

  public UserAccessHandler(AuthClient client, UserInfo userInfo) {
    authClient = client;
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

  private void processAuthFailure(RoutingContext event, String failureMessage) {
    LOGGER.error("Error : Authentication Failure : {}", failureMessage);
    if (failureMessage.equalsIgnoreCase("User information is invalid")) {
      LOGGER.error("User information is invalid");
      event.fail(new DxRuntimeException(HttpStatusCode.INTERNAL_SERVER_ERROR.getValue(), INTERNAL_SERVER_ERROR));
    }
  event.fail(new DxRuntimeException(HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN));
  }


}
