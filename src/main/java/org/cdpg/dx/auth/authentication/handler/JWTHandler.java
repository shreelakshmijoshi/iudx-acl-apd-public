package org.cdpg.dx.auth.authentication.handler;


import static org.cdpg.dx.common.models.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.cdpg.dx.auth.authentication.service.JWTService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.auth.authentication.exception.AuthenticationException;
import org.cdpg.dx.common.exception.DxRuntimeException;
import org.cdpg.dx.common.models.HttpStatusCode;
import org.cdpg.dx.common.models.JwtData;
import org.cdpg.dx.util.RoutingContextHelper;

/** DX Authentication handler to authenticate token passed in HEADER */
public class JWTHandler implements Handler<RoutingContext> {
    private static final Logger LOGGER = LogManager.getLogger(JWTHandler.class);
    private final JWTService jwtService;

  public JWTHandler(JWTService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void handle(RoutingContext context) {
    String token = iudx.apd.acl.server.common.RoutingContextHelper.getToken(context);
    LOGGER.debug("Info :{}", context.request().path());
    Future<JwtData> jwtDataFuture = jwtService.decodeToken(token);

    jwtDataFuture
        .onSuccess(
            jwtData -> {
              RoutingContextHelper.setJwtData(context, jwtData);
              context.next();
            })
        .onFailure(
            failure -> {
              LOGGER.error("Token decode failed: {}", failure.getMessage());
              context.fail(
                  new DxRuntimeException(
                      HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN));
            });
  }
}
