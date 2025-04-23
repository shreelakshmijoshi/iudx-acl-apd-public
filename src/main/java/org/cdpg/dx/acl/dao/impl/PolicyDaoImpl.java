package org.cdpg.dx.acl.dao.impl;

import static org.cdpg.dx.acl.dao.util.Constants.*;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cdpg.dx.acl.dao.PolicyDao;
import org.cdpg.dx.acl.dao.model.PolicyDto;
import org.cdpg.dx.database.postgres.models.Condition;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;
import org.cdpg.dx.database.postgres.service.PostgresService;

/**
 * Check where is the policy related queries to the table are done What are the arguments required
 * to query the table what is the output type
 */
public class PolicyDaoImpl implements PolicyDao {
  SelectQuery selectQuery;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  Condition condition;
  List<Object> value;
  PostgresService postgresService;

  public PolicyDaoImpl() {
    selectQuery = new SelectQuery();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    condition = new Condition();
    value = new ArrayList<>();
  }

  public PolicyDaoImpl(
      SelectQuery selectQuery,
      InsertQuery insertQuery,
      UpdateQuery updateQuery,
      Condition condition) {
    this.condition = condition;
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
  }

  @Override
  public Future<List<PolicyDto>> getPolicyFromDb(PolicyDto policyDto, boolean isUpdatedAtDesc) {
    condition.setOperator(Condition.Operator.EQUALS);
    List<String> column = new ArrayList<>();
    JsonObject jsonObject = policyDto.toJson();
    for (Map.Entry<String, Object> json : jsonObject) {
      if (json.getValue() != null) {
        condition.setColumn(json.getKey());
        value.add(json.getValue());
        column.add(json.getKey());
      }
    }
    selectQuery
        .setCondition(condition)
        .setColumns(column)
        .setTable(POLICY_TABLE)
        .setLimit(null)
        .setOffset(null);

    if (isUpdatedAtDesc) {
      selectQuery.setGroupBy(List.of(DB_UPDATED_AT));
    }

    //    if(policyId != null){
    //      condition.setColumn("_id");
    //      value.add(policyId);
    //    }
    //    if(ownerId != null){
    //      condition.setColumn("owner_id");
    //      value.add(ownerId);
    //    }
    //    if(consumerEmailId != null){
    //      condition.setColumn("user_emailid");
    //      value.add(consumerEmailId);
    //    }
    //    if(status != null){
    //      condition.setColumn("status");
    //      value.add(status);
    //    }

    //    condition.setValues(value);
    //    selectQuery.setColumns(List.of("*"));
    //    selectQuery.setTable(POLICY_TABLE);
    //    selectQuery.setCondition(condition);
    //    Map<String, Object> paramMap = new HashMap<>();
    //    paramMap.put(DB_ID, policyId);
    //    paramMap.put(DB_OWNER_ID, ownerId);
    //    paramMap.put(DB_CONSUMER_EMAIL, consumerEmailId);

  return postgresService
        .select(selectQuery)
        .compose(
            queryResult -> {
              List<PolicyDto> policyDtos = queryResult.getRows().getList();
              return Future.succeededFuture(policyDtos);
            }).recover(Future::failedFuture);

  }

  @Override
  public Future<PolicyDto> createPolicyInDb(
      String consumerEmailId,
      String itemId,
      String ownerId,
      String expiryAt,
      JsonObject constraints,
      String status) {
    insertQuery.setTable(POLICY_TABLE);
    insertQuery.setColumns(POLICY_TABLE_COLUMNS);
    insertQuery.setValues(List.of(consumerEmailId, itemId,ownerId, expiryAt, constraints, status));
    return postgresService
        .select(selectQuery)
        .compose(
            queryResult -> {
              PolicyDto policyDtos = new PolicyDto(JsonObject.mapFrom(queryResult.getRows().getList().getFirst()));
              return Future.succeededFuture(policyDtos);
            })
        .recover(Future::failedFuture);
  }

  @Override
  public Future<PolicyDto> deletePolicyInDb(String policyId, String expiryAt) {
    condition.setColumn(DB_EXPIRY_AT);
    condition.setValues(List.of(expiryAt));
    updateQuery.setCondition(condition);
    updateQuery.setLimit(null);
    updateQuery.setTable(POLICY_TABLE);
    updateQuery.setOrderBy(null);
    return postgresService
        .update(updateQuery)
        .compose(
            queryResult -> {
              PolicyDto policyDtos = new PolicyDto(JsonObject.mapFrom(queryResult.getRows().getList().getFirst()));
              return Future.succeededFuture(policyDtos);
            })
        .recover(Future::failedFuture);
  }
}
