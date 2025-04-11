package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.ResourceEntityDto;
import org.cdpg.dx.database.postgres.models.Condition;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;

public class ResourceEntityDaoImpl implements ResourceEntityDao{
  SelectQuery selectQuery;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  Condition condition;

  public ResourceEntityDaoImpl(){
    selectQuery = new SelectQuery();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    condition = new Condition();
  }
  public ResourceEntityDaoImpl(SelectQuery selectQuery, InsertQuery insertQuery, UpdateQuery updateQuery, Condition condition){
    this.condition = condition;
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
  }
  @Override
  public Future<ResourceEntityDto> getResourceFromDb(JsonObject info) {
    return null;
  }

  @Override
  public Future<ResourceEntityDto> createResourceInDb(JsonObject entry) {
    return null;
  }

  @Override
  public Future<ResourceEntityDto> deleteResourceInDb(String policyId) {
    return null;
  }

  @Override
  public Future<ResourceEntityDto> updateResourceInDb(JsonObject info) {
    return null;
  }
}
