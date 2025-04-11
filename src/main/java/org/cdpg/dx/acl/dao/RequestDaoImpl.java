package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.RequestDto;
import org.cdpg.dx.database.postgres.models.Condition;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;

public class RequestDaoImpl implements RequestDao{
  SelectQuery selectQuery;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  Condition condition;

  public RequestDaoImpl(){
    selectQuery = new SelectQuery();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    condition = new Condition();
  }
  public RequestDaoImpl(SelectQuery selectQuery, InsertQuery insertQuery, UpdateQuery updateQuery, Condition condition){
    this.condition = condition;
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
  }
  @Override
  public Future<RequestDto> getRequestFromDb(JsonObject info) {
    return null;
  }

  @Override
  public Future<RequestDto> createRequestInDb(JsonObject entry) {
    return null;
  }

  @Override
  public Future<RequestDto> deleteRequestInDb(String policyId) {
    return null;
  }

  @Override
  public Future<RequestDto> updateRequestInDb(JsonObject info) {
    return null;
  }
}
