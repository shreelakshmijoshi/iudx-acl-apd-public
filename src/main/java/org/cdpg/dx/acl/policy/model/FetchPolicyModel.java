package org.cdpg.dx.acl.policy.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.policy.util.ItemType;
import org.cdpg.dx.common.models.Response;

@DataObject
@JsonGen(publicConverter = false)
public class FetchPolicyModel {
  String policyId;
  String itemId;
  ItemType itemType;
  String expiryAt;
  Constraints constraints;
  String status;
  String updatedAt;
  String createdAt;
  User consumer;
  User provider;
  public FetchPolicyModel(){}

  public FetchPolicyModel(FetchPolicyModel other)
  {
    this.policyId = other.getPolicyId();
    this.itemId = other.getItemId();
    this.itemType = other.getItemType();
    this.expiryAt = other.getExpiryAt();
    this.constraints = other.getConstraints();
    this.status = other.getStatus();
    this.updatedAt = other.getUpdatedAt();
    this.createdAt = other.getCreatedAt();
    this.consumer = other.getConsumer();
    this.provider = other.getProvider();

  }
  //Deserialization
  public FetchPolicyModel(JsonObject jsonObject){
    FetchPolicyModelConverter.fromJson(jsonObject, this);
  }

  /**
   * Converts Data object or Fetch Policy class object to json object [Serialization]
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    FetchPolicyModelConverter.toJson(this, jsonObject);
    return jsonObject;
  }
  public String getPolicyId() {
    return policyId;
  }

  public void setPolicyId(String policyId) {
    this.policyId = policyId;
  }

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public String getExpiryAt() {
    return expiryAt;
  }

  public void setExpiryAt(String expiryAt) {
    this.expiryAt = expiryAt;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public void setConstraints(Constraints constraints) {
    this.constraints = constraints;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public User getConsumer() {
    return consumer;
  }

  public void setConsumer(User consumer) {
    this.consumer = consumer;
  }

  public User getProvider() {
    return provider;
  }

  public void setProvider(User provider) {
    this.provider = provider;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  private static class User{
    String email;
    Name name;
    String id;

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public Name getName() {
      return name;
    }

    public void setName(Name name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  private static class Name{
    String firstName;
    String lastName;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
  }

   public static class Constraints {
    private String[] access;

    public String[] getAccess() { return access; }
    public void setAccess(String[] value) { this.access = value; }
  }
}
