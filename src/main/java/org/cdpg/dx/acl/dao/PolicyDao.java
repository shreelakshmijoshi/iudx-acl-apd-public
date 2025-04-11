package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.dao.model.PolicyDto;
import org.cdpg.dx.acl.policy.model.Policy;

public interface PolicyDao {

  //TODO: Change json object info or the method param with the required type according to the classes

  /**
   * Returns the policy info by querying with either policyId, ownerId or consumerId
   * @param policyId Optional parameter
   * @param ownerId Optional parameter
   * @param consumerEmailId Optional parameter
   * @return
   */
  public Future<PolicyDto> getPolicyFromDb(String policyId, String ownerId, String consumerEmailId);
  public Future<PolicyDto> createPolicyInDb(JsonObject entry);
  public Future<PolicyDto> deletePolicyInDb(String policyId);
}
