package iudx.apd.acl.server.aaaService;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import iudx.apd.acl.server.apiserver.util.User;

public interface AuthClientInterface {
  Future<User> fetchUserInfo(JsonObject jsonObject);
}
