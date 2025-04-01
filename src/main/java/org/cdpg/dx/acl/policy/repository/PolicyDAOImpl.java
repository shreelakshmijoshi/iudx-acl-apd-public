package org.cdpg.dx.acl.policy.repository;

import io.vertx.core.Future;
import org.cdpg.dx.acl.policy.model.Policy;
import org.cdpg.dx.acl.policy.model.PolicyUpdateDTO;
import org.cdpg.dx.acl.policy.util.Constants;
import org.cdpg.dx.database.postgres.models.*;
import org.cdpg.dx.database.postgres.service.PostgresService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PolicyDAOImpl implements PolicyDAO {
    private final PostgresService postgresService;

    public PolicyDAOImpl(PostgresService postgresService) {
        this.postgresService = postgresService;
    }

    @Override
    public Future<Policy> create(Policy policy) {
        Map<String, Object> policyMap = policy.toNonEmptyFieldsMap();

        List<String> columns = policyMap.keySet().stream().toList();
        List<Object> values = policyMap.values().stream().toList();

        InsertQuery query = new InsertQuery(Constants.POLICY_TABLE, columns, values);

        return postgresService.insert(query)
                .compose(result -> {
                    if (result.getRows().isEmpty()) {
                        return Future.failedFuture("Insert query returned no rows.");
                    }
                    return Future.succeededFuture(Policy.fromJson(result.getRows().getJsonObject(0)));
                })
                .recover(err -> {
                    System.err.println("Error inserting policy: " + err.getMessage());
                    return Future.failedFuture(err);
                });
    }

    @Override
    public Future<Boolean> update(UUID id, PolicyUpdateDTO updateDTO) {
        Map<String, Object> feildsMap = updateDTO.toNonEmptyFieldsMap();

        if (feildsMap.isEmpty()) {
            return Future.failedFuture(new IllegalArgumentException("No fields to update"));
        }

        List<String> columns = List.copyOf(feildsMap.keySet());
        List<Object> values = List.copyOf(feildsMap.values());

        // Create Condition for WHERE clause
        Condition condition = new Condition(Constants.POLICY_ID, Condition.Operator.EQUALS, List.of(id));

        // Build the UpdateQuery
        UpdateQuery query = new UpdateQuery(Constants.POLICY_TABLE, columns, values, condition, null, null);


        return postgresService.update(query)
                .compose(result -> {
                    if (!result.isRowsAffected()) {
                        return Future.failedFuture("No rows updated");
                    }
                    return Future.succeededFuture(true);
                })
                .recover(err -> {
                    System.err.println("Error updating policy: "+ id.toString() + " msg:"+ err.getMessage());
                    return Future.failedFuture(err);
                });
    }
    @Override
    public Future<Policy> getById(UUID id) {

        List<String> columns = Constants.ALL_POLICY_FIELDS;;
        // Create Condition for WHERE clause
        Condition condition = new Condition(Constants.POLICY_ID, Condition.Operator.EQUALS, List.of(id));


        SelectQuery query = new SelectQuery(Constants.POLICY_TABLE, columns,condition,null, null,null,null);

        return postgresService.select(query)
                .compose(result -> {
                    if (result.getRows().isEmpty()) {
                        return Future.failedFuture("select query returned no rows.");
                    }
                    return Future.succeededFuture(Policy.fromJson(result.getRows().getJsonObject(0)));
                })
                .recover(err -> {
                    System.err.println("Error inserting policy: " + err.getMessage());
                    return Future.failedFuture(err);
                });
    }

    @Override
    public Future<Boolean> delete(UUID id) {

        // Create Condition for WHERE clause
        Condition condition = new Condition(Constants.POLICY_ID, Condition.Operator.EQUALS, List.of(id));

        // Build the UpdateQuery
        DeleteQuery query = new DeleteQuery(Constants.POLICY_TABLE, condition, null, null);


        return postgresService.delete(query)
                .compose(result -> {
                    if (!result.isRowsAffected()) {
                        return Future.failedFuture("No rows updated");
                    }
                    return Future.succeededFuture(true);
                })
                .recover(err -> {
                    System.err.println("Error updating policy: "+ id.toString() + " msg:"+ err.getMessage());
                    return Future.failedFuture(err);
                });
    }
}
