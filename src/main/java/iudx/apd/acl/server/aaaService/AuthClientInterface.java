package iudx.apd.acl.server.aaaService;

import io.vertx.core.Future;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.aclAuth.model.UserInfo;

public interface AuthClientInterface {
  Future<User> fetchUserInfo(UserInfo userInfo);
}
