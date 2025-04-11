package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.cdpg.dx.acl.dao.model.PolicyDto;
import org.cdpg.dx.database.postgres.models.Condition;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;

/**
 * Check where is the policy related queries to the table are done
 * What are the arguments required to query the table
 * what is the output type
 */
public class PolicyDaoImpl implements PolicyDao{
  SelectQuery selectQuery;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  Condition condition;
  List<String> value;

  public PolicyDaoImpl(){
    selectQuery = new SelectQuery();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    condition = new Condition();
    value = new ArrayList<>();
  }
  public PolicyDaoImpl(SelectQuery selectQuery, InsertQuery insertQuery, UpdateQuery updateQuery, Condition condition){
    this.condition = condition;
    this.selectQuery = selectQuery;
    this.updateQuery = updateQuery;
    this.insertQuery = insertQuery;
  }
  @Override
  public Future<PolicyDto> getPolicyFromDb(String policyId, String ownerId, String consumerEmailId) {
    condition.setOperator(Condition.Operator.EQUALS);
    if(policyId != null){
      condition.setColumn("_id");
      value.add(policyId);
    }
    if(ownerId != null){
      condition.setColumn("owner_id");
      value.add(ownerId);
    }
    if(consumerEmailId != null){
      condition.setColumn("user_emailid");
      value.add(consumerEmailId);
    }
    selectQuery.setColumns("*");


    return null;
  }

  @Override
  public Future<PolicyDto> createPolicyInDb(JsonObject entry) {
    return null;
  }

  @Override
  public Future<PolicyDto> deletePolicyInDb(String policyId) {
    return null;
  }
}
