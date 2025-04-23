package org.cdpg.dx.acl.dao.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.policy.model.Constraints;

@DataObject(generateConverter = true)
public class AccessRequestDto {
  String id;
  String consumerId;
  String ownerId;
  String status;
  String itemId;
  String expiryAt;
  String createdAt;
  String updatedAt;
  JsonObject additionalInfo;
  Constraints constraints;

  public AccessRequestDto(){}
  public AccessRequestDto(AccessRequestDto other){
    this.id = other.getId();
    this.additionalInfo = other.getAdditionalInfo();
    this.itemId = other.getItemId();
    this.expiryAt = other.getExpiryAt();
    this.constraints = other.getConstraints();
    this.updatedAt = other.getUpdatedAt();
    this.createdAt = other.getCreatedAt();
    this.consumerId = other.getConsumerId();
    this.ownerId = other.getOwnerId();
    this.status = other.getStatus();

  }
  public AccessRequestDto(JsonObject jsonObject) {
    /* Converts JsonObject to PolicyDto class object or dataObject conversion [Deserialization] */
    AccessRequestDtoConverter.fromJson(jsonObject, this);
  }

  /**
   * Converts Data object or Policy class object to json object [Serialization]
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    AccessRequestDtoConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(String consumerId) {
    this.consumerId = consumerId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getExpiryAt() {
    return expiryAt;
  }

  public void setExpiryAt(String expiryAt) {
    this.expiryAt = expiryAt;
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

  public JsonObject getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(JsonObject additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public void setConstraints(Constraints constraints) {
    this.constraints = constraints;
  }
}
