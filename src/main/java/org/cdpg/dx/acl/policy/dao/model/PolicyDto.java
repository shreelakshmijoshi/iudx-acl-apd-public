package org.cdpg.dx.acl.policy.dao.model;

import static org.cdpg.dx.acl.policy.dao.util.Constants.*;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.Column;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import java.util.Objects;


public class PolicyDto {
  @Column(name = DB_ID)
  private String policyId;

  @Column(name = DB_CONSUMER_EMAIL)
  private String consumerEmailId;

  @Column(name = DB_ITEM_ID)
  private String itemId;

  @Column(name = DB_OWNER_ID)
  private String ownerId;

  @Column(name = DB_STATUS)
  private String policyStatus;

  @Column(name = DB_EXPIRY_AT)
  private String expiryAt;

  @Column(name = DB_CONSTRAINTS)
  private JsonObject constraints;

  public PolicyDto() {}

  public PolicyDto(PolicyDto other) {
    this.policyId = other.getPolicyId();
    this.consumerEmailId = other.getConsumerEmailId();
    this.itemId = other.getItemId();
    this.ownerId = other.getOwnerId();
    this.policyStatus = other.getPolicyStatus();
    this.expiryAt = other.getExpiryAt();
    this.constraints = other.getConstraints();
  }

  public PolicyDto(JsonObject jsonObject) {
    /* Converts JsonObject to PolicyDto class object or dataObject conversion [Deserialization] */
    setPolicyId(jsonObject.getString(DB_POLICY_ID));
//    setPolicyStatus(jsonObject.getString(DB_STATUS));
//    setConstraints(jsonObject.getJsonObject(DB_CONSTRAINTS));
//    setConsumerEmailId(jsonObject.getString(DB_CONSUMER_EMAIL));
//    setItemId(jsonObject.getString(DB_ITEM_ID));
//    setPolicyStatus(jsonObject.getString(DB_STATUS));
//    setExpiryAt(jsonObject.getString(DB_EXPIRY_AT));

//    PolicyDtoConverter.fromJson(jsonObject, this);
  }

  /**
   * Converts Data object or Policy class object to json object [Serialization]
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject jsonObject =
        new JsonObject()
            .put(DB_ID, this.getPolicyId())
            .put(DB_CONSUMER_EMAIL, getConsumerEmailId())
            .put(DB_ITEM_ID, getItemId())
            .put(DB_OWNER_ID, getOwnerId())
            .put(DB_STATUS, getPolicyStatus())
            .put(DB_EXPIRY_AT, getExpiryAt())
            .put(DB_CONSTRAINTS, getConstraints());
//    PolicyDtoConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getPolicyId() {
    return policyId;
  }

  public PolicyDto setPolicyId(String policyId) {
    this.policyId = policyId;
    return this;
  }

  public String getConsumerEmailId() {
    return consumerEmailId;
  }

  public PolicyDto setConsumerEmailId(String consumerEmailId) {
    this.consumerEmailId = consumerEmailId;
    return this;
  }

  public String getItemId() {
    return itemId;
  }

  public PolicyDto setItemId(String itemId) {
    this.itemId = itemId;
    return this;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public PolicyDto setOwnerId(String ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  public String getPolicyStatus() {
    return policyStatus;
  }

  public PolicyDto setPolicyStatus(String policyStatus) {
    this.policyStatus = policyStatus;
    return this;
  }

  public String getExpiryAt() {
    return expiryAt;
  }

  public PolicyDto setExpiryAt(String expiryAt) {
    this.expiryAt = expiryAt;
    return this;
  }

  public JsonObject getConstraints() {
    return constraints;
  }

  public PolicyDto setConstraints(JsonObject constraints) {
    this.constraints = constraints;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PolicyDto policy)) return false;
    return Objects.equals(policyId, policy.policyId)
        && Objects.equals(consumerEmailId, policy.consumerEmailId)
        && Objects.equals(itemId, policy.itemId)
        && Objects.equals(ownerId, policy.ownerId)
        && Objects.equals(policyStatus, policy.policyStatus)
        && Objects.equals(expiryAt, policy.expiryAt)
        && Objects.equals(constraints, policy.constraints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        policyId, consumerEmailId, itemId, ownerId, policyStatus, expiryAt, constraints);
  }
}
