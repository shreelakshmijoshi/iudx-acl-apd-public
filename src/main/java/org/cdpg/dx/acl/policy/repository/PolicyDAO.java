package org.cdpg.dx.acl.policy.repository;

import io.vertx.core.Future;
import org.cdpg.dx.acl.policy.model.Policy;
import org.cdpg.dx.acl.policy.model.PolicyDTO;
import org.cdpg.dx.acl.policy.model.PolicyUpdateDTO;

import java.util.UUID;



public interface PolicyDAO {
    Future<Policy> create(Policy policy);

    Future<Policy> getById(String policyId);

    Future<Boolean> update(String id, PolicyUpdateDTO updateDTO);

    Future<Boolean> delete(String policyId); // Return boolean for clarity
}