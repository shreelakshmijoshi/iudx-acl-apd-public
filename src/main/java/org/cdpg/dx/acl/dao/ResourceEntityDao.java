package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.RequestDto;
import org.cdpg.dx.acl.dao.model.ResourceEntityDto;

public interface ResourceEntityDao {
  //TODO: Change json object info or the method param with the required type according to the classes
  public Future<ResourceEntityDto> getResourceFromDb(JsonObject info);
  public Future<ResourceEntityDto> createResourceInDb(JsonObject entry);
  public Future<ResourceEntityDto> deleteResourceInDb(String policyId);
  public Future<ResourceEntityDto> updateResourceInDb(JsonObject info);

}
