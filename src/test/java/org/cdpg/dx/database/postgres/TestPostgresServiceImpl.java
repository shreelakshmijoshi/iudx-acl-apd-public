package org.cdpg.dx.database.postgres;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import iudx.apd.acl.server.policy.TestCreatePolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.database.postgres.models.DeleteQuery;
import org.cdpg.dx.database.postgres.models.InsertQuery;
import org.cdpg.dx.database.postgres.models.SelectQuery;
import org.cdpg.dx.database.postgres.models.UpdateQuery;
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
public class TestPostgresServiceImpl {
  private static final Logger LOGGER = LogManager.getLogger(TestCreatePolicy.class);
  PostgresService postgresService;
  @Mock
  InsertQuery insertQuery;
  @Mock
  UpdateQuery updateQuery;
  @Mock
  DeleteQuery deleteQuery;
  @Mock
  SelectQuery selectQuery;
  @Container
  PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:12.11");
  @BeforeEach
  public void setUp(VertxTestContext vertxTestContext){
    Util util = new Util();
    postgresService = util.setUp(container);
    vertxTestContext.completeNow();
  }

  @Test
  @DisplayName("Test insert method")
  public void testInsert(VertxTestContext vertxTestContext){
    postgresService.insert(insertQuery);
    vertxTestContext.completeNow();
  }
  @Test
  @DisplayName("Test delete method")
  public void testDelete(VertxTestContext vertxTestContext){
    postgresService.delete(deleteQuery);
    vertxTestContext.completeNow();
  }
  @Test
  @DisplayName("Test update method")
  public void testUpdate(VertxTestContext vertxTestContext){
    postgresService.update(updateQuery);
    vertxTestContext.completeNow();
  }
  @Test
  @DisplayName("Test select method")
  public void testSelect(VertxTestContext vertxTestContext){
    postgresService.select(selectQuery);
    vertxTestContext.completeNow();
  }
}
