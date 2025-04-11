package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.ResourceEntityDto;
import org.cdpg.dx.acl.dao.model.UserDto;

public interface UserDao {
  //TODO: Change json object info or the method param with the required type according to the classes
  public Future<UserDto> getResourceFromDb(JsonObject info);
  public Future<UserDto> createResourceInDb(JsonObject entry);
  public Future<UserDto> deleteResourceInDb(String policyId);
  public Future<UserDto> updateResourceInDb(JsonObject info);

}
