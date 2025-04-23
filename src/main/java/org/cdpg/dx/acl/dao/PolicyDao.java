package org.cdpg.dx.acl.dao;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.cdpg.dx.acl.dao.model.PolicyDto;

@DataObject
@JsonGen
public interface PolicyDao {

  /**
   * Returns the policy info by querying with either policyId, ownerId or consumerId
   *
   * @param policyId Optional parameter
   * @param ownerId Optional parameter
   * @param consumerEmailId Optional parameter
   * @param status Optional parameter
   * @param itemId Optional parameter
   * @param expiryAt Optional parameter
   * @param isUpdatedAtDesc Optional parameter
   * @return List of PolicyDto
   */
//  public Future<List<PolicyDto>> getPolicyFromDb(
//      String policyId,
//      String ownerId,
//      String consumerEmailId,
//      String status,
//      String itemId,
//      String expiryAt,
//      boolean isUpdatedAtDesc);
  public Future<List<PolicyDto>> getPolicyFromDb(
      PolicyDto policyDto,
      boolean isUpdatedAtDesc);

//  public Future<List<PolicyDto>> getPolicyForUserFromDb(String ownerId, String itemId, String consumerEmailId, boolean isUpdatedAtDesc);
//  public Future<PolicyDto> checkExistingPolicyFromDb(String ownerId, String itemId, String consumerEmailId, String status, String expiryAt, String constraints);
//public Future<List<PolicyDto>> getActiveConsumerPolicyFromDB(String itemId, String consumerEmailId, String expiryAt, String status);


  public Future<PolicyDto> createPolicyInDb(
      String consumerEmailId,
      String itemId,
      String ownerId,
      String expiryAt,
      JsonObject constraints,
      String status);

  public Future<PolicyDto> deletePolicyInDb(String policyId, String expiryAt);
}
