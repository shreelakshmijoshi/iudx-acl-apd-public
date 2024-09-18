package iudx.apd.acl.server.common;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.apiserver.util.Constants.API_METHOD;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.authentication.model.JwtData;
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

  public static JsonObject getAuthInfo(RoutingContext routingContext){
    return
        new JsonObject()
        .put(API_ENDPOINT, getRequestPath(routingContext))
        .put(HEADER_TOKEN, getToken(routingContext))
        .put(API_METHOD, getMethod(routingContext));
  }

  public static String getToken(RoutingContext routingContext)
  {
    return routingContext.request().headers().get(AUTHORIZATION_KEY);
  }

  public static String getMethod(RoutingContext routingContext)
  {
    return routingContext.request().method().toString();
  }

  public static String getRequestPath(RoutingContext routingContext)
  {
    return routingContext.request().path();
  }

  public static void setJwtData(RoutingContext routingContext, JwtData jwtData)
  {
    routingContext.put(JWT_DATA, jwtData);
  }

  public static JwtData getJwtData(RoutingContext routingContext)
  {
    return routingContext.get(JWT_DATA);
  }

  public static void setApis(RoutingContext routingContext, Api api)
  {
    routingContext.put(API, api);
  }

  public static Api getApis(RoutingContext routingContext)
  {
    return routingContext.get(API);
  }


}
