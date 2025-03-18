package org.cdpg.dx.database.postgres.models;

class Filter {
    private String field;
    private String operator;
    private Object value;

    public Filter(String field, String operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public String toSQL() {
        return field + " " + operator + " ?";
    }

    public Object getValue() {
        return value;
    }
}
