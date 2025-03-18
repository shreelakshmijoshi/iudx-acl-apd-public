package org.cdpg.dx.database.postgres.models;


import java.util.List;

public record DeleteQuery(String table, String condition, List<Object> values) implements Query {
    @Override
    public String toSQL() {
        return "DELETE FROM " + table + " WHERE " + condition;
    }

    @Override
    public List<Object> getQueryParams() {
        return values;
    }
}