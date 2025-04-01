package org.cdpg.dx.acl.policy.repository;

import io.vertx.core.Future;
import org.cdpg.dx.acl.policy.model.Policy;
import org.cdpg.dx.acl.policy.model.PolicyUpdateDTO;

import java.util.UUID;


public interface PolicyDAO {

    Future<Policy> create(Policy policy);

    Future<Policy> getById(UUID policyId);

    Future<Boolean> update(UUID id, PolicyUpdateDTO updateDTO);

    Future<Boolean> delete(UUID policyId); // Return boolean for clarity
}