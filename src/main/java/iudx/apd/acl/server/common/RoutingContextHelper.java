package iudx.apd.acl.server.common;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.API_METHOD;
import static iudx.apd.acl.server.common.ResponseUrn.INVALID_TOKEN_URN;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.authentication.model.JwtData;
import iudx.apd.acl.server.validation.exceptions.DxRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RoutingContextHelper {
  private static final Logger LOGGER = LogManager.getLogger(RoutingContextHelper.class);
  private static final String JWT_DATA = "jwtData";

  public static void setUser(RoutingContext routingContext, User user) {
    routingContext.put(USER, user);
  }

  public static User getUser(RoutingContext routingContext) {
    return routingContext.get(USER);
  }

  public static JsonObject getAuthInfo(RoutingContext routingContext) {
    return new JsonObject()
        .put(API_ENDPOINT, getRequestPath(routingContext))
        .put(HEADER_TOKEN, getToken(routingContext))
        .put(API_METHOD, getMethod(routingContext));
  }

  /* token would can be of the type : Bearer <JWT-Token> or <JWT-Token> */
  /* allowing both the tokens to be authenticated for now */
  /* TODO: later, 401 error is thrown if the token does not contain Bearer keyword */
  public static String getToken(RoutingContext routingContext) {
    String token = routingContext.request().headers().get(AUTHORIZATION_KEY);
    boolean isItABearerToken = token.contains(HEADER_TOKEN_BEARER);
    if (isItABearerToken && token.trim().split(" ").length == 2) {
      String[] tokenWithoutBearer = token.split(HEADER_TOKEN_BEARER);
      token = tokenWithoutBearer[1].replaceAll("\\s", "");
    }
    return token;
  }

  public static JsonObject getVerifyAuthInfo(RoutingContext routingContext) {
    return new JsonObject()
        .put(API_ENDPOINT, getRequestPath(routingContext))
        .put(HEADER_TOKEN, getVerifyToken(routingContext))
        .put(API_METHOD, getMethod(routingContext));
  }

  private static String getVerifyToken(RoutingContext routingContext) {
    String token = routingContext.request().headers().get(AUTHORIZATION_KEY);
    if (token.trim().split(" ").length == 2) {
      token = token.trim().split(" ")[1];
      return token;
    } else {
     throw new DxRuntimeException(HttpStatusCode.getByValue(401).getValue(), INVALID_TOKEN_URN);
    }
  }

  public static String getMethod(RoutingContext routingContext) {
    return routingContext.request().method().toString();
  }

  public static String getRequestPath(RoutingContext routingContext) {
    return routingContext.request().path();
  }

  public static void setJwtData(RoutingContext routingContext, JwtData jwtData) {
    routingContext.put(JWT_DATA, jwtData);
  }

  public static JwtData getJwtData(RoutingContext routingContext) {
    return routingContext.get(JWT_DATA);
  }
}
