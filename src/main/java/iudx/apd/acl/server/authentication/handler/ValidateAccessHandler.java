package iudx.apd.acl.server.authentication.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.authentication.model.DxRole;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.RoutingContextHelper;
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValidateAccessHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = LogManager.getLogger(ValidateAccessHandler.class);


  /**
   * Something has happened, so handle it.
   *
   * @param event the event to handle
   */
  @Override
  public void handle(RoutingContext event) {
    event.next();
  }

  public Handler<RoutingContext> setUserRolesForEndpoint(DxRole... roleForApi) {
    return context -> handleWithRoles(context, roleForApi);
}

  private void handleWithRoles(RoutingContext event, DxRole[] roleForApi) {
        JwtData jwtData = RoutingContextHelper.getJwtData(event);
    DxRole userRole = DxRole.fromRole(jwtData);
    boolean isUserAllowedToAccessApi = Arrays.asList(roleForApi).contains(userRole);
    if (!isUserAllowedToAccessApi) {
       event.fail(new DxRuntimeException(
           HttpStatusCode.UNAUTHORIZED.getValue(),
           ResponseUrn.INVALID_TOKEN_URN,
           "No access provided to endpoint"));
    }
    event.next();
  }
}
