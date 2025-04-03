package org.cdpg.dx.aaa.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cdpg.dx.aaa.client.AAAClient;
import org.cdpg.dx.common.models.User;
import org.cdpg.dx.aaa.models.UserInfo;
import org.cdpg.dx.aaa.util.AAAConstants;

/**
 * Implementation of AAAService for handling user authentication.
 */
public class AAAServiceImpl implements AAAService {

  private static final Logger LOGGER = LogManager.getLogger(AAAServiceImpl.class);
  private final AAAClient authWebClient;

  public AAAServiceImpl(AAAClient authWebClient) {
    this.authWebClient = authWebClient;
  }

  @Override
  public Future<User> fetchUserInfo(UserInfo userInfo) {
    String userId = userInfo.userId().toString();
    String role = userInfo.role().name();
    String resourceServer = userInfo.audience();

    LOGGER.info("Fetching user info for userId: {}", userId);

    return authWebClient.fetchUserData(userId, role, resourceServer)
        .compose(this::mapToUser)
        .recover(error -> {
          LOGGER.error("Failed to fetch user info: {}", error.getMessage());
          return Future.failedFuture("Unable to retrieve user information.");
        });
  }

  /**
   * Maps validated authentication response to a User object.
   */
  private Future<User> mapToUser(JsonObject result) {
    try {
      if (result.getString(AAAConstants.USER_ID) == null ||
          result.getString(AAAConstants.ROLE) == null ||
          result.getString(AAAConstants.EMAIL) == null ||
          result.getJsonObject(AAAConstants.NAME) == null ||
          result.getJsonObject(AAAConstants.NAME).getString(AAAConstants.FIRST_NAME) == null ||
          result.getJsonObject(AAAConstants.NAME).getString(AAAConstants.LAST_NAME) == null ||
          result.getString(AAAConstants.RESOURCE_SERVER) == null) {
        throw new IllegalArgumentException("Missing required user fields: " + result.encode());
      }
  
//      User user = new User(
//          result.getString(AAAConstants.USER_ID),
//          DxRole.fromString(result.getString(AAAConstants.ROLE)),
//          result.getString(AAAConstants.EMAIL),
//          result.getJsonObject(AAAConstants.NAME).getString(AAAConstants.FIRST_NAME),
//          result.getJsonObject(AAAConstants.NAME).getString(AAAConstants.LAST_NAME),
//          result.getString(AAAConstants.RESOURCE_SERVER)
//      );
  
//      return Future.succeededFuture(user);
      return null;
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid user data received: {}", result.encode());
      return Future.failedFuture(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error parsing user data: {}", e.getMessage(), e);
      return Future.failedFuture("Failed to process user data");
    }
  }
}
