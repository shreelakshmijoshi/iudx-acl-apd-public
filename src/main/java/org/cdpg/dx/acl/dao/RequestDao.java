package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.RequestDto;
import org.cdpg.dx.acl.policy.model.Policy;

public interface RequestDao {
  //TODO: Change json object info or the method param with the required type according to the classes
  public Future<RequestDto> getRequestFromDb(JsonObject info);
  public Future<RequestDto> createRequestInDb(JsonObject entry);
  public Future<RequestDto> deleteRequestInDb(String policyId);
  public Future<RequestDto> updateRequestInDb(JsonObject info);

}
