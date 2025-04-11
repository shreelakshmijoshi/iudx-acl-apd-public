package org.cdpg.dx.acl.dao.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class ResourceEntityDto {
  String id;
  String providerId;
  String resourceGroupId;
  String itemType;
  String resourceServerUrl;
  String createdAt;
  String updatedAt;
  public ResourceEntityDto(){}
  public ResourceEntityDto(ResourceEntityDto other){
    this.id = other.getId();
    this.providerId = other.getProviderId();
    this.resourceGroupId = other.getResourceGroupId();
    this.itemType = other.getItemType();
    this.resourceServerUrl = other.getResourceServerUrl();
    this.updatedAt = other.getUpdatedAt();
    this.createdAt = other.getCreatedAt();
  }
    public ResourceEntityDto(JsonObject jsonObject) {
      /* Converts JsonObject to PolicyDto class object or dataObject conversion [Deserialization] */
      ResourceEntityDtoConverter.fromJson(jsonObject, this);
    }

    /**
     * Converts Data object or Policy class object to json object [Serialization]
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      ResourceEntityDtoConverter.toJson(this, jsonObject);
      return jsonObject;

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public String getResourceGroupId() {
    return resourceGroupId;
  }

  public void setResourceGroupId(String resourceGroupId) {
    this.resourceGroupId = resourceGroupId;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getResourceServerUrl() {
    return resourceServerUrl;
  }

  public void setResourceServerUrl(String resourceServerUrl) {
    this.resourceServerUrl = resourceServerUrl;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
}
