package org.cdpg.dx.database.postgres.models;

import java.util.List;

public record SelectQuery(String table, List<String> columns, Condition condition) implements Query {
    @Override
    public String toSQL() {
        String columnNames = String.join(", ", columns);
        return "SELECT " + columnNames + " FROM " + table + (condition != null ? " WHERE " + condition.toSQL() : "");
    }

    @Override
    public List<Object> getQueryParams() {
        return condition != null ? condition.getValues() : List.of();
    }
}
