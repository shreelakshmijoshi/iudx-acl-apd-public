package org.cdpg.dx.aaa.client;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client for communicating with the authentication server.
 */
public class AAAWebClient implements AAAClient {

  private static final Logger LOGGER = LogManager.getLogger(AAAWebClient.class);
  private final WebClient client;
  private final String authHost;
  private final String authServerSearchPath;
  private final String clientId;
  private final String clientSecret;
  private final int authPort;

  public AAAWebClient(JsonObject config, WebClient webClient) {
    this.client = webClient;
    this.authHost = config.getString("authHost");
    this.authServerSearchPath = config.getString("dxAuthBasePath") + "/search";
    this.clientId = config.getString("clientId");
    this.clientSecret = config.getString("clientSecret");
    this.authPort = config.getInteger("authPort");
  }

  /**
   * Fetches and validates user data from the authentication server.
   */
  public Future<JsonObject> fetchUserData(String userId, String role, String resourceServer) {
    LOGGER.info("Fetching user data for userId: {}, role: {}, resourceServer: {}", userId, role, resourceServer);

    return client.get(authPort, authHost, authServerSearchPath)
        .putHeader("clientId", clientId)
        .putHeader("clientSecret", clientSecret)
        .addQueryParam("role", role)
        .addQueryParam("userId", userId)
        .addQueryParam("resourceServer", resourceServer)
        .send()
        .compose(this::handleResponse)
        .recover(error -> {
          LOGGER.error("Failed to fetch user data for userId {}: {}", userId, error.getMessage());
          return Future.failedFuture(error);
        });
  }

  /**
   * Handles and validates the response from the authentication server.
   */
  private Future<JsonObject> handleResponse(HttpResponse<Buffer> response) {
    int statusCode = response.statusCode();
    
    if (statusCode < 200 || statusCode >= 300) {
      LOGGER.warn("Auth Server request failed - Status: {} - {}", statusCode, response.statusMessage());
      return Future.failedFuture("Auth Server request failed with status: " + statusCode);
    }

    JsonObject responseBody = response.bodyAsJsonObject();
    LOGGER.debug("Auth Server Response: {}", responseBody.encodePrettily());


    if (!"urn:dx:as:Success".equals(responseBody.getString("type"))) {
      LOGGER.warn("User not found in Auth.");
      return Future.failedFuture("User not present in Auth.");
    }

    JsonObject result = responseBody.getJsonObject("results");
    if (result == null) {
      LOGGER.error("Auth response does not contain 'results' field");
      return Future.failedFuture("Invalid Auth response: missing 'results'");
    }

    return Future.succeededFuture(result);
  }
}
