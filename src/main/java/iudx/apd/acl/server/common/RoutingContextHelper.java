package iudx.apd.acl.server.common;

import static iudx.apd.acl.server.apiserver.util.Constants.USER;

import io.vertx.ext.web.RoutingContext;
import iudx.apd.acl.server.apiserver.util.User;

public class RoutingContextHelper {
  private RoutingContext routingContext;


  public void setUser(RoutingContext routingContext, User user)
  {
    routingContext.put(USER, user);
  }

  public User getUser()
  {
    return routingContext.get(USER);
  }

}
