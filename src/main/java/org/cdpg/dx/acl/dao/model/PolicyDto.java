package org.cdpg.dx.acl.dao.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import org.cdpg.dx.acl.policy.model.Role;

@DataObject(generateConverter = true)
public class PolicyDto {
  private String policyId;
  private Role consumerEmailId;
  private String itemId;
  private String ownerId;
  private String policyStatus;
  private String expiryAt;
  private JsonObject constraints;

  public PolicyDto(){}
  public PolicyDto(PolicyDto other){
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
      PolicyDtoConverter.fromJson(jsonObject, this);
    }

    /**
     * Converts Data object or Policy class object to json object [Serialization]
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
      JsonObject jsonObject = new JsonObject();
      PolicyDtoConverter.toJson(this, jsonObject);
      return jsonObject;
    }

    public String getPolicyId() {
      return policyId;
    }

    public void setPolicyId(String policyId) {
      this.policyId = policyId;
    }

    public Role getConsumerEmailId() {
      return consumerEmailId;
    }

    public void setConsumerEmailId(Role consumerEmailId) {
      this.consumerEmailId = consumerEmailId;
    }

    public String getItemId() {
      return itemId;
    }

    public void setItemId(String itemId) {
      this.itemId = itemId;
    }

    public String getOwnerId() {
      return ownerId;
    }

    public void setOwnerId(String ownerId) {
      this.ownerId = ownerId;
    }

    public String getPolicyStatus() {
      return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
      this.policyStatus = policyStatus;
    }

    public String getExpiryAt() {
      return expiryAt;
    }

    public void setExpiryAt(String expiryAt) {
      this.expiryAt = expiryAt;
    }

    public JsonObject getConstraints() {
      return constraints;
    }

    public void setConstraints(JsonObject constraints) {
      this.constraints = constraints;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof PolicyDto policy)) return false;
      return Objects.equals(policyId, policy.policyId)
          && consumerEmailId == policy.consumerEmailId
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


