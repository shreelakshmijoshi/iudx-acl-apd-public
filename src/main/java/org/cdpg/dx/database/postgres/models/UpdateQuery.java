package org.cdpg.dx.database.postgres.models;

import java.util.List;

public record UpdateQuery(String table, List<String> columns, List<Object> values, Condition condition) implements Query {
    @Override
    public String toSQL() {
        String setClause = String.join(" = ?, ", columns) + " = ?";
        return "UPDATE " + table + " SET " + setClause + (condition != null ? " WHERE " + condition.toSQL() : "");
    }

    @Override
    public List<Object> getQueryParams() {
        List<Object> params = values;
        if (condition != null) {
            params.addAll(condition.getValues());
        }
        return params;
    }
}
