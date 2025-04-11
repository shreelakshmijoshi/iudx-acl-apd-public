package org.cdpg.dx.acl.dao.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class ApprovedAccessRequestDto {
  String id;
  String policyId;
  String requestId;
  String createdAt;
  String updatedAt;

  public ApprovedAccessRequestDto() {}

  public ApprovedAccessRequestDto(ApprovedAccessRequestDto other) {
    this.id = other.getId();
    this.createdAt = other.getCreatedAt();
    this.updatedAt = other.getUpdatedAt();
    this.policyId = other.getPolicyId();
    this.requestId = other.getRequestId();
  }

  public ApprovedAccessRequestDto(JsonObject jsonObject) {
    /* Converts JsonObject to PolicyDto class object or dataObject conversion [Deserialization] */
    ApprovedAccessRequestDtoConverter.fromJson(jsonObject, this);
  }

  /**
   * Converts Data object or Policy class object to json object [Serialization]
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    ApprovedAccessRequestDtoConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
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
