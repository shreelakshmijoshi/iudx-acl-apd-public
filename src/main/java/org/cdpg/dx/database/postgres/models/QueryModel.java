package org.cdpg.dx.database.postgres.models;

import java.util.List;

public interface QueryModel {
    String toSQL();
    List<Object> getQueryParams();
}

