package org.cdpg.dx.acl.policy.dao.impl;

import static org.cdpg.dx.acl.policy.dao.util.Constants.*;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.acl.policy.dao.PolicyDao;
import org.cdpg.dx.acl.policy.dao.model.PolicyDto;
import org.cdpg.dx.acl.policy.model.Constraints;
import org.cdpg.dx.acl.policy.util.Status;
import org.cdpg.dx.database.postgres.models.*;
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
  PostgresService postgresService;
  private static final Logger LOGGER = LogManager.getLogger(PolicyDao.class);

  public PolicyDaoImpl(
      PostgresService postgresService,
      SelectQuery selectQuery,
      InsertQuery insertQuery,
      UpdateQuery updateQuery,
      Condition condition) {
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
    this.condition = condition;
    this.postgresService = postgresService;
    selectQuery.setTable(POLICY_TABLE);
    updateQuery.setTable(POLICY_TABLE);
    insertQuery.setTable(POLICY_TABLE);
  }

  @Override
  public Future<PolicyDto> getActiveConsumerPolicyFromDB(String itemId, String consumerEmailId) {
 condition = new Condition(List.of(
     new Condition()
         .setColumn(DB_CONSUMER_EMAIL)
         .setValues(List.of(consumerEmailId))
         .setOperator(Condition.Operator.EQUALS),
     new Condition()
         .setColumn(DB_ITEM_ID)
         .setValues(List.of(itemId))
         .setOperator(Condition.Operator.EQUALS),
     new Condition()
         .setColumn(DB_EXPIRY_AT)
         .setValues(List.of(LocalDateTime.now()))
         .setOperator(Condition.Operator.GREATER),
     new Condition()
         .setColumn(DB_STATUS)
         .setValues(List.of(Status.ACTIVE))
         .setOperator(Condition.Operator.EQUALS)), Condition.LogicalOperator.AND);
//    condition.setConditions(
//        List.of(
//            new Condition()
//                .setColumn(DB_CONSUMER_EMAIL)
//                .setValues(List.of(consumerEmailId))
//                .setOperator(Condition.Operator.EQUALS),
//            new Condition()
//                .setColumn(DB_ITEM_ID)
//                .setValues(List.of(itemId))
//                .setOperator(Condition.Operator.EQUALS),
//            new Condition()
//                .setColumn(DB_EXPIRY_AT)
//                .setValues(List.of("NOW()"))
//                .setOperator(Condition.Operator.GREATER),
//            new Condition()
//                .setColumn(DB_STATUS)
//                .setValues(List.of(Status.ACTIVE))
//                .setOperator(Condition.Operator.EQUALS)));

    //    condition.setValues(List.of(consumerEmailId, itemId, "NOW()", Status.ACTIVE));
    selectQuery.setColumns(List.of("*")).setCondition(condition);
    return postgresService
        .select(selectQuery)
        .compose(
            queryResult -> {
              PolicyDto policyDto = new PolicyDto(queryResult.getRows().getJsonObject(0));
              return Future.succeededFuture(policyDto);
            })
        .recover(
            throwable -> {
              return Future.failedFuture(throwable.getMessage());
            });
  }

  @Override
  public Future<PolicyDto> checkExistingPolicyFromDb(
      String itemId, String ownerId, Status status, String consumerEmailId) {
    condition.setConditions(
        List.of(
            new Condition()
                .setColumn(DB_CONSUMER_EMAIL)
                .setValues(List.of(consumerEmailId))
                .setOperator(Condition.Operator.EQUALS),
            new Condition()
                .setColumn(DB_OWNER_ID)
                .setValues(List.of(ownerId))
                .setOperator(Condition.Operator.EQUALS),
            new Condition()
                .setColumn(DB_ITEM_ID)
                .setValues(List.of(itemId))
                .setOperator(Condition.Operator.EQUALS),
            new Condition()
                .setColumn(DB_EXPIRY_AT)
                .setValues(List.of("NOW()"))
                .setOperator(Condition.Operator.GREATER),
            new Condition()
                .setColumn(DB_STATUS)
                .setValues(List.of(status))
                .setOperator(Condition.Operator.EQUALS)));
    selectQuery.setColumns(List.of("*")).setCondition(condition);
    return postgresService
        .select(selectQuery)
        .compose(
            queryResult -> {
              PolicyDto policyDto = new PolicyDto(queryResult.getRows().getJsonObject(0));
              return Future.succeededFuture(policyDto);
            })
        .recover(
            throwable -> {
              return Future.failedFuture(throwable.getMessage());
            });
  }

  @Override
  public Future<PolicyDto> createPolicyInDb(
      String consumerEmailId,
      String itemId,
      String ownerId,
      String expiryAt,
      Constraints constraints) {
    Map<String, Object> queryParam =
        Map.of(
            DB_CONSUMER_EMAIL, consumerEmailId,
            DB_ITEM_ID, itemId,
            DB_OWNER_ID, ownerId,
            DB_EXPIRY_AT, expiryAt,
            DB_CONSTRAINTS, constraints,
            DB_STATUS, Status.ACTIVE);
    insertQuery
        .setColumns(new ArrayList<>(queryParam.keySet()))
        .setValues(new ArrayList<>(queryParam.values()));
    return postgresService
        .insert(insertQuery)
        .compose(
            queryResult -> {
              PolicyDto policyDto = new PolicyDto(queryResult.getRows().getJsonObject(0));
              return Future.succeededFuture(policyDto);
            })
        .recover(
            throwable -> {
              return Future.failedFuture(throwable.getMessage());
            });
  }

  @Override
  public Future<PolicyDto> deletePolicyInDb(String policyId) {
//    condition
//        .setColumn(DB_EXPIRY_AT)
//        .setOperator(Condition.Operator.GREATER)
//        .setValues(List.of("NOW()"));
//    updateQuery.setCondition(condition).setColumns(List.of(DB_ID)).setValues(List.of(policyId));
//    UpdateQuery updateQuery1 = new UpdateQuery(POLICY_TABLE, List.of(DB_STATUS), List.of("DELETED"),null, null,null);
    updateQuery.setColumns(List.of(DB_STATUS)).setValues(List.of(Status.DELETED)).setCondition(condition.setColumn(DB_POLICY_ID).setValues(List.of(policyId)).setOperator(
        Condition.Operator.EQUALS));

    LOGGER.error("Update query is : {}", updateQuery.toSQL());
//        LOGGER.error("Select query is : {}", selectQuery.toSQL());

    return postgresService
        .update(updateQuery)
        .compose(
            queryResult -> {
              LOGGER.error("Result is : {}", queryResult.getRows());
              PolicyDto policyDto = new PolicyDto(new JsonObject().put("_id","klfjkfgjfg"));
              return Future.succeededFuture(policyDto);
            })
        .recover(failure -> Future.failedFuture(failure.getMessage()));
  }
}
