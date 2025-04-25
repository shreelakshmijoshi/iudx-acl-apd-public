package org.cdpg.dx.policy;

import static org.cdpg.dx.acl.policy.dao.util.Constants.DB_EXPIRY_AT;
import static org.cdpg.dx.acl.policy.dao.util.Constants.DB_ID;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import iudx.apd.acl.server.policy.TestCreatePolicy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.acl.policy.dao.impl.PolicyDaoImpl;
import org.cdpg.dx.database.postgres.Util;
import org.cdpg.dx.database.postgres.models.*;
import org.cdpg.dx.database.postgres.service.PostgresService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith({MockitoExtension.class, VertxExtension.class})
public class TestPolicyDaoImpl {
  private static final Logger LOGGER = LogManager.getLogger(TestPolicyDaoImpl.class);
  PostgresService postgresService;
  InsertQuery insertQuery;
  UpdateQuery updateQuery;
  SelectQuery selectQuery;
  Util util;
  Condition condition;
  @Container PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:12.11");
  private PolicyDaoImpl policyDao;

  @BeforeEach
  public void setUp(VertxTestContext vertxTestContext) {
    this.util = new Util();
    postgresService = util.setUp(container);
//    util.testInsert();
    insertQuery = new InsertQuery();
    updateQuery = new UpdateQuery();
    selectQuery = new SelectQuery();
    condition = new Condition();
    policyDao =
        new PolicyDaoImpl(postgresService, selectQuery, insertQuery, updateQuery, condition);
    vertxTestContext.completeNow();
  }

  @Test
  @DisplayName("Test getActiveConsumerPolicyFromDB method : Success")
  public void testGetActiveConsumerPolicyFromDB(VertxTestContext vertxTestContext) {
    policyDao
        .getActiveConsumerPolicyFromDB(util.getResourceId().toString(), util.getConsumerEmailId())
        .onComplete(
            handler -> {
              if(handler.succeeded()){
                LOGGER.info(handler.result());
                vertxTestContext.completeNow();
              }else {
                vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
              }
            });
  }

  @Test
  @DisplayName("Test checkExistingPolicyFromDb method : Success")
  public void testCheckExistingPolicyFromDbe(VertxTestContext vertxTestContext) {

    policyDao
        .getActiveConsumerPolicyFromDB(util.getResourceId().toString(), util.getConsumerEmailId())
        .onComplete(
            handler -> {
              if(handler.succeeded()){
                LOGGER.info(handler.result());
                vertxTestContext.completeNow();
              }else {
                vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
              }
            });
  }

  @Test
  @DisplayName("Test createPolicyInDb method : Success")
  public void testCreatePolicyInDb(VertxTestContext vertxTestContext) {

    policyDao
        .getActiveConsumerPolicyFromDB(util.getResourceId().toString(), util.getConsumerEmailId())
        .onComplete(
            handler -> {
              if(handler.succeeded()){
                LOGGER.info(handler.result());
                vertxTestContext.completeNow();
              }else {
                vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
              }
            });
  }

  @Test
  @DisplayName("Test deletePolicyInDb method : Success")
  public void testDeletePolicyInDb(VertxTestContext vertxTestContext) {

    policyDao
        .getActiveConsumerPolicyFromDB(util.getResourceId().toString(), util.getConsumerEmailId())
        .onComplete(
            handler -> {
              if(handler.succeeded()){
                LOGGER.info(handler.result());
                vertxTestContext.completeNow();
              }else {
                vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
              }
            });
  }

  @Test
  @DisplayName("Test update method")
  public void testUpdate(VertxTestContext vertxTestContext) {
    policyDao
        .createPolicyInDb(util.getConsumerEmailId(), util.getResourceId().toString(), null,null,null)
        .onComplete(
            handler -> {
              if(handler.succeeded()){
                LOGGER.info(handler.result());
                vertxTestContext.completeNow();
              }else {
                vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
              }
            });
  }

  @Test
  @DisplayName("Test select method")
  public void testSelect(VertxTestContext vertxTestContext) {

    util.testInsert().onComplete(ancd -> {
      if(ancd.succeeded()){
        policyDao
            .deletePolicyInDb(util.getPolicyId().toString())
            .onComplete(
                handler -> {
                  if(handler.succeeded()){
                    LOGGER.info(handler.result());
                    vertxTestContext.completeNow();
                  }else {
                    vertxTestContext.failNow("Failed to delete : " + handler.cause().getMessage());
                  }
                });
      }else {
        LOGGER.error("Empty database");
      }
    });

  }
}
