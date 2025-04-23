package org.cdpg.dx.database.postgres.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import java.util.List;

@DataObject
@JsonGen
public class InsertQuery implements Query {
    private String table;
    private List<String> columns;
    private List<Object> values;

    // Default constructor (Needed for deserialization)
    public InsertQuery() {}
    public InsertQuery(InsertQuery other){
        this.table = other.getTable();
        this.columns = other.getColumns();
        this.values = other.getValues();
    }

    // JSON Constructor
    public InsertQuery(JsonObject json) {
        InsertQueryConverter.fromJson(json, this);  // Use generated converter
    }

    // Convert to JSON
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        InsertQueryConverter.toJson(this, json);
        return json;
    }

    // Getters & Setters (Required for DataObject)
    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }

    public List<Object> getValues() { return values; }
    public void setValues(List<Object> values) { this.values = values; }

    @Override
    public String toSQL() {
        String placeholders = "?,".repeat(columns.size()).replaceAll(",$", "");
        return "INSERT INTO " + table + " (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";
    }

    @Override
    public List<Object> getQueryParams() {
        return values;
    }
}