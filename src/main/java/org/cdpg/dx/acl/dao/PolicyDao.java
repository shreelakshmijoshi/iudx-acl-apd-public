package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.List;
import org.cdpg.dx.acl.dao.model.PolicyDto;

public interface PolicyDao {

  // TODO: Change json object info or the method param with the required type according to the
  // classes

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
  public Future<List<PolicyDto>> getPolicyFromDb(
      String policyId,
      String ownerId,
      String consumerEmailId,
      String status,
      String itemId,
      String expiryAt,
      boolean isUpdatedAtDesc);

  public Future<PolicyDto> createPolicyInDb(
      String consumerEmailId,
      String itemId,
      String ownerId,
      String expiryAt,
      JsonObject constraints,
      String status);

  public Future<PolicyDto> deletePolicyInDb(String policyId, String expiryAt);
}
