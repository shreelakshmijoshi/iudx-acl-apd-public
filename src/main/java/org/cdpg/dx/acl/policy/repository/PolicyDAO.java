package org.cdpg.dx.acl.policy.repository;

import io.vertx.core.Future;
import org.cdpg.dx.acl.policy.model.PolicyDTO;
import org.cdpg.dx.acl.policy.model.PolicyUpdateDTO;

import java.util.UUID;


public interface PolicyDAO {

    Future<PolicyDTO> create(PolicyDTO policyDTO);

    Future<PolicyDTO> getById(UUID policyId);

    Future<Boolean> update(UUID id, PolicyUpdateDTO updateDTO);

    Future<Boolean> delete(UUID policyId); // Return boolean for clarity
}