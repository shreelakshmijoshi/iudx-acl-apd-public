package iudx.apd.acl.server.policy.service;

import static iudx.apd.acl.server.apiserver.util.Constants.*;
import static iudx.apd.acl.server.common.HttpStatusCode.*;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import iudx.apd.acl.server.apiserver.util.User;
import iudx.apd.acl.server.common.DxRuntimeException;
import iudx.apd.acl.server.common.HttpStatusCode;
import iudx.apd.acl.server.common.ResponseUrn;
import iudx.apd.acl.server.common.response.RestResponse;
import iudx.apd.acl.server.database.PostgresService;
import iudx.apd.acl.server.policy.util.Constants;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.cdpg.dx.acl.policy.service.model.Response;
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
   * Executes delete policy by setting the status field in record to DELETED from ACTIVE and by
   * checking if the policy is expired
   *
   * @param query SQL query to update the status of the policy
   * @param policyUuid policy id as type UUID
   * @return The response of the query execution
   */
  private Future<Response> executeUpdateQuery(String query, UUID policyUuid) {
    LOG.debug("inside executeUpdateQuery");
    Promise<Response> promise = Promise.promise();
    Tuple tuple = Tuple.of(policyUuid);
    this.executeQuery(query, tuple)
        .onSuccess(
            response -> {
              /* policy has expired */
              if (response.getJsonArray(RESULT).isEmpty()) {
                String detail = FAILURE_MESSAGE + " , as policy is expired";
                throw new DxRuntimeException(
                    BAD_REQUEST.getValue(), ResponseUrn.BAD_REQUEST_URN, detail);
              } else {
                LOG.info("update query succeeded");
                String detail = "Policy deleted successfully";
                Response restResponse =
                    new Response()
                        .setStatusCode(SUCCESS.getValue())
                        .setTitle(SUCCESS.getUrn())
                        .setType(ResponseUrn.SUCCESS_URN.getUrn())
                        .setDetail(detail);
                promise.complete(restResponse);
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

  /**
   * Executes the respective queries
   *
   * @param query SQL Query to be executed
   * @param tuple exchangeable(s) for the query
   * @return JsonObject Result of the query execution is sent as Json Object in a Future
   */
  public Future<JsonObject> executeQuery(String query, Tuple tuple) {

    Pool pool = postgresService.getPool();
    Collector<Row, ?, List<JsonObject>> rowListCollector =
        Collectors.mapping(row -> row.toJson(), Collectors.toList());
    Promise<JsonObject> promise = Promise.promise();
    pool.withConnection(
            sqlConnection ->
                sqlConnection
                    .preparedQuery(query)
                    .collecting(rowListCollector)
                    .execute(tuple)
                    .map(rows -> rows.value()))
        .onSuccess(
            successHandler -> {
              JsonArray response = new JsonArray(successHandler);
              JsonObject responseJson =
                  new JsonObject()
                      .put(TYPE, org.cdpg.dx.common.models.ResponseUrn.SUCCESS_URN.getUrn())
                      .put(TITLE, org.cdpg.dx.common.models.ResponseUrn.SUCCESS_URN.getMessage())
                      .put(RESULT, response);
              promise.complete(responseJson);
            })
        .onFailure(
            failureHandler -> {
              LOG.error("Failure while executing the query : {}", failureHandler.getMessage());
              String detail = "Failure while executing query";
              throw new DxRuntimeException(
                  INTERNAL_SERVER_ERROR.getValue(), ResponseUrn.DB_ERROR_URN, detail);
            });
    return promise.future();
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
  private Future<Boolean> verifyPolicy(User user, String query, UUID policyUuid) {
    LOG.debug("inside verifyPolicy");
    Promise<Boolean> promise = Promise.promise();
    String ownerId = user.getUserId();
    LOG.trace("What's the ownerId : " + ownerId);
    Tuple tuple = Tuple.of(policyUuid);
    Future<JsonObject> queryFuture = executeQuery(query, tuple);
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
   * Acts as an entry point for count query and update query execution
   *
   * @param policy to be deleted
   * @return result of the execution as Json Object
   */
  public Future<Response> initiateDeletePolicy(JsonObject policy, User user) {
    UUID policyUuid = UUID.fromString(policy.getString("id"));
    Future<Boolean> policyVerificationFuture =
        verifyPolicy(user, Constants.CHECK_IF_POLICY_PRESENT_QUERY, policyUuid);
    return policyVerificationFuture.compose(
        isVerified -> {
          if (isVerified) {
            return executeUpdateQuery(Constants.DELETE_POLICY_QUERY, policyUuid);
          }
          return Future.failedFuture(policyVerificationFuture.cause().getMessage());
        });
  }
}
