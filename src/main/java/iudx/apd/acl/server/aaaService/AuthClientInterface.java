package iudx.apd.acl.server.aaaService;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.authentication.model.UserInfo;

public interface AuthClientInterface {
  Future<User> fetchUserInfo(UserInfo userInfo);
}
