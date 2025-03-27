package org.cdpg.dx.acl.policy.service;

import static org.cdpg.dx.util.Constants.*;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import iudx.apd.acl.server.database.postgres.service.PostgresService;
import java.util.UUID;
import org.cdpg.dx.common.exception.DxRuntimeException;
import org.cdpg.dx.common.models.HttpStatusCode;
import org.cdpg.dx.common.models.ResponseUrn;
import org.cdpg.dx.common.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletePolicy {
  private static final Logger LOG = LoggerFactory.getLogger(DeletePolicy.class);
  private static final String FAILURE_MESSAGE = "Policy could not be deleted";
  private final PostgresService postgresService;

  public DeletePolicy(PostgresService postgresService) {
    this.postgresService = postgresService;
  }

  /**
   * Queries postgres table to check if the policy given in the request is owned by the provider or
   * provider delegate, Checks if the policy that is about to be deleted is ACTIVE or DELETED Checks
   * If one of the policy id fails any of the checks, it returns false
   *
   * @param query SQL query
   * @param policyUuid list of policies of UUID
   * @return true if qualifies all the checks
   */
  public Future<Boolean> verifyPolicy(User user, String query, UUID policyUuid) {
    LOG.debug("inside verifyPolicy");
    Promise<Boolean> promise = Promise.promise();
    String ownerId = user.getUserId();
    LOG.trace("What's the ownerId : " + ownerId);
    JsonObject queryParam = new JsonObject().put(ID, policyUuid);
    Future<JsonObject> queryFuture = postgresService.executePreparedQuery(query, queryParam);
    queryFuture
        .onSuccess(
            response -> {
              if (response.getJsonArray(RESULT).isEmpty()) {
                String detail = FAILURE_MESSAGE + ", as it doesn't exist";
                throw new DxRuntimeException(
                    HttpStatusCode.NOT_FOUND.getValue(),
                    ResponseUrn.RESOURCE_NOT_FOUND_URN,
                    detail);
              } else {
                JsonObject result = response.getJsonArray(RESULT).getJsonObject(0);
                String rsServerUrl = result.getString("resource_server_url");
                String ownerIdValue = result.getString("owner_id");
                String status = result.getString("status");
                /* does the policy belong to the owner who is requesting */
                if (!rsServerUrl.equalsIgnoreCase(user.getResourceServerUrl())) {
                  LOG.error("Failure : OwnerShip error, rsServerUrl does not match");
                  String detail =
                      "Access Denied: You do not have ownership rights for this policy.";
                  throw new DxRuntimeException(
                      HttpStatusCode.FORBIDDEN.getValue(), ResponseUrn.FORBIDDEN_URN, detail);
                } else if (ownerIdValue.equals(ownerId)) {
                  /* is policy in ACTIVE status */
                  if (status.equals("ACTIVE")) {
                    LOG.info("Success : policy verified");
                    promise.complete(true);
                  } else {
                    LOG.error("Failure : policy is not active");
                    String detail = FAILURE_MESSAGE + ", as policy is not ACTIVE";
                    throw new DxRuntimeException(
                        HttpStatusCode.BAD_REQUEST.getValue(), ResponseUrn.BAD_REQUEST_URN, detail);
                  }
                } else {
                  LOG.error("Failure : policy does not belong to the user");
                  String detail = FAILURE_MESSAGE + ", as policy doesn't belong to the user";
                  throw new DxRuntimeException(
                      HttpStatusCode.FORBIDDEN.getValue(), ResponseUrn.FORBIDDEN_URN, detail);
                }
              }
            })
        .onFailure(
            throwable -> {
              LOG.error("Failed {}", throwable.getCause().getMessage());
              promise.fail(throwable.getCause().getMessage());
            });

    return promise.future();
  }

  /**
   * Executes delete policy by setting the status field in record to DELETED from ACTIVE and by
   * checking if the policy is expired
   *
   * @param query SQL query to update the status of the policy
   * @param policyId with type UUID
   * @return The response of the query execution
   */
  public Future<Void> executeDeletePolicy(String query, UUID policyId) {
    LOG.debug("inside justDelete");
    Promise<Void> promise = Promise.promise();
    JsonObject queryParam = new JsonObject().put(ID, policyId);
    postgresService
        .executePreparedQuery(query, queryParam)
        .onSuccess(
            response -> {
              /* policy has expired */
              if (response.getJsonArray(RESULT).isEmpty()) {
                String detail = FAILURE_MESSAGE + " , as policy is expired";
                throw new DxRuntimeException(
                    HttpStatusCode.BAD_REQUEST.getValue(), ResponseUrn.BAD_REQUEST_URN, detail);
              } else {
                LOG.info("update query succeeded");
                promise.complete();
              }
            })
        .onFailure(
            throwable -> {
              LOG.debug("update query failed");
              String detail = FAILURE_MESSAGE + ", update query failed";
              throw new DxRuntimeException(
                  HttpStatusCode.BAD_REQUEST.getValue(), ResponseUrn.BAD_REQUEST_URN, detail);
            });
    return promise.future();
  }
}
