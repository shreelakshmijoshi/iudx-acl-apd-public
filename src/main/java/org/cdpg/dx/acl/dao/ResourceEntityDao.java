package org.cdpg.dx.acl.dao;

import io.vertx.core.Future;
import java.util.List;
import org.cdpg.dx.acl.dao.model.ResourceEntityDto;

public interface ResourceEntityDao {

  public Future<List<ResourceEntityDto>> getResourceFromDb(
      String resourceId, String resourceServerUrl, String providerId);

  public Future<ResourceEntityDto> createResourceInDb(
      String resourceId,
      String providerId,
      String resourceGroupId,
      String itemType,
      String resourceServerUrl,
      boolean onConflictDoNothing);

  public Future<List<ResourceEntityDto>> checkResourcesFromDb(List<String> resourceId);
}
