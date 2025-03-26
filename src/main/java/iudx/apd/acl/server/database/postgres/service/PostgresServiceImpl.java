package iudx.apd.acl.server.database.postgres.service;

import static iudx.apd.acl.server.apiserver.util.Constants.*;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import iudx.apd.acl.server.common.ResponseUrn;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgresServiceImpl implements PostgresService {
  private static final Logger LOGGER = LogManager.getLogger(PostgresServiceImpl.class);

  private final Pool client;

  public PostgresServiceImpl(final Pool pgclient) {
    this.client = pgclient;
  }

  private static Future<Void> executeBatch(
      SqlConnection conn, ConcurrentLinkedQueue<String> statements) {
    try {
      var statement = statements.poll();
      if (statement == null) {
        return Future.succeededFuture();
      }
      Promise<Void> promise = Promise.promise();

      Collector<Row, ?, List<JsonObject>> rowCollector =
          Collectors.mapping(row -> row.toJson(), Collectors.toList());
      conn.query(statement)
          .collecting(rowCollector)
          .execute()
          .map(rows -> rows.value())
          .onSuccess(
              successHandler -> {
                executeBatch(conn, statements)
                    .onComplete(
                        h -> {
                          promise.complete();
                        });
              })
          .onFailure(
              failureHandler -> {
                LOGGER.debug("Failure : {}", failureHandler.getMessage());
                LOGGER.error("Fail db");
                promise.fail(failureHandler);
              });
      return promise.future();
    } catch (Throwable t) {
      return Future.failedFuture(t);
    }
  }

  @Override
  public Future<JsonObject> executeQuery(final String query) {
    Promise<JsonObject> promise = Promise.promise();

    Collector<Row, ?, List<JsonObject>> rowCollector =
        Collectors.mapping(row -> row.toJson(), Collectors.toList());

    client
        .withConnection(
            connection ->
                connection.query(query).collecting(rowCollector).execute().map(row -> row.value()))
        .onSuccess(
            successHandler -> {
              JsonArray result = new JsonArray(successHandler);
              JsonObject responseJson =
                  new JsonObject()
                      .put(TYPE, ResponseUrn.SUCCESS_URN.getUrn())
                      .put(TITLE, ResponseUrn.SUCCESS_URN.getMessage())
                      .put(RESULT, result);
              promise.complete(responseJson);
            })
        .onFailure(
            failureHandler -> {
              LOGGER.debug("Failure : {}", failureHandler.getMessage());
              String response =
                  new JsonObject()
                      .put(TYPE, ResponseUrn.DB_ERROR_URN.getUrn())
                      .put(TITLE, ResponseUrn.DB_ERROR_URN.getMessage())
                      .put(DETAIL, failureHandler.getLocalizedMessage())
                      .encode();
              promise.fail(response);
            });
    return promise.future();
  }

  public Future<JsonObject> executeCountQuery(final String query) {
    Promise<JsonObject> promise = Promise.promise();

    client
        .withConnection(
            connection ->
                connection.query(query).execute().map(rows -> rows.iterator().next().getInteger(0)))
        .onSuccess(
            count -> {
              promise.complete(new JsonObject().put("totalHits", count));
            })
        .onFailure(
            failureHandler -> {
              LOGGER.debug("Failure : {}", failureHandler.getMessage());
              String response =
                  new JsonObject()
                      .put(TYPE, ResponseUrn.DB_ERROR_URN.getUrn())
                      .put(TITLE, ResponseUrn.DB_ERROR_URN.getMessage())
                      .put(DETAIL, failureHandler.getLocalizedMessage())
                      .encode();
              promise.fail(response);
            });
    return promise.future();
  }

  @Override
  public Future<JsonObject> executeTransaction(final List<String> queries) {
    Promise<JsonObject> promise = Promise.promise();

    client
        .withTransaction(
            connection -> {
              ConcurrentLinkedQueue<String> statements = new ConcurrentLinkedQueue<>(queries);
              return executeBatch(connection, statements);
            })
        .onComplete(
            completeHandler -> {
              if (completeHandler.succeeded()) {
                LOGGER.debug("transaction successful");
                JsonObject responseJson =
                    new JsonObject()
                        .put(TYPE, ResponseUrn.SUCCESS_URN.getUrn())
                        .put(TITLE, ResponseUrn.SUCCESS_URN.getMessage());
                promise.complete(responseJson);
              } else {
                LOGGER.debug("transaction failed");
                LOGGER.debug("Failure : {}", completeHandler.cause().getMessage());
                String response =
                    new JsonObject()
                        .put(TYPE, ResponseUrn.DB_ERROR_URN.getUrn())
                        .put(TITLE, ResponseUrn.DB_ERROR_URN.getMessage())
                        .put(DETAIL, completeHandler.cause().getMessage())
                        .encode();
                promise.fail(response);
              }
            });
    return promise.future();
  }

  // TODO : prepared query works only for String parameters, due to service proxy restriction with
  // allowed type as arguments. needs to work with TupleBuilder class which will parse other types
  // like date appropriately to match with postgres types
  @Override
  public Future<JsonObject> executePreparedQuery(final String query, final JsonObject queryParams) {
    Promise<JsonObject> promise = Promise.promise();
    List<Object> params = new ArrayList<Object>(queryParams.getMap().values());

    Tuple tuple = Tuple.from(params);

    Collector<Row, ?, List<JsonObject>> rowCollector =
        Collectors.mapping(row -> row.toJson(), Collectors.toList());

    client
        .withConnection(
            connection ->
                connection
                    .preparedQuery(query)
                    .collecting(rowCollector)
                    .execute(tuple)
                    .map(rows -> rows.value()))
        .onSuccess(
            successHandler -> {
              JsonArray response = new JsonArray(successHandler);
              JsonObject responseJson =
                  new JsonObject()
                      .put(TYPE, ResponseUrn.SUCCESS_URN.getUrn())
                      .put(TITLE, ResponseUrn.SUCCESS_URN.getMessage())
                      .put(RESULT, response);
              ;
              promise.complete(responseJson);
            })
        .onFailure(
            failureHandler -> {
              LOGGER.debug("Failure : {}", failureHandler.getMessage());
              String response =
                  new JsonObject()
                      .put(TYPE, ResponseUrn.DB_ERROR_URN.getUrn())
                      .put(TITLE, ResponseUrn.DB_ERROR_URN.getMessage())
                      .put(DETAIL, failureHandler.getCause().getMessage())
                      .encode();
              promise.fail(response);
            });
    return promise.future();
  }
}
