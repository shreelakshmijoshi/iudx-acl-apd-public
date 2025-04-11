package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.UserDto;
import org.cdpg.dx.database.postgres.models.Condition;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;

public class UserDaoImpl implements UserDao{
  SelectQuery selectQuery;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  Condition condition;

  public UserDaoImpl(){
    selectQuery = new SelectQuery();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    condition = new Condition();
  }
  public UserDaoImpl(SelectQuery selectQuery, InsertQuery insertQuery, UpdateQuery updateQuery, Condition condition){
    this.condition = condition;
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
  }

  @Override
  public Future<UserDto> getResourceFromDb(JsonObject info) {
    return null;
  }

  @Override
  public Future<UserDto> createResourceInDb(JsonObject entry) {
    return null;
  }

  @Override
  public Future<UserDto> deleteResourceInDb(String policyId) {
    return null;
  }

  @Override
  public Future<UserDto> updateResourceInDb(JsonObject info) {
    return null;
  }
}
