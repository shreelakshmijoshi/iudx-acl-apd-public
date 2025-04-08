package org.cdpg.dx.acl.policy.model;
import java.util.Optional;
import java.util.UUID;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.policy.util.Constants;
import java.util.Map;
import java.util.HashMap;


public record PolicyDTO(
        Optional<UUID> policyId,
        String userEmailId,
        UUID itemId,
        UUID ownerId,
        String status,
        String expiryAt,
        Optional<String> createdAt,
        Optional<String> updatedAt,
        JsonObject constraints
) {
    public static PolicyDTO fromJson(JsonObject policyDetails) {
        return new PolicyDTO(
                Optional.ofNullable(policyDetails.getString(Constants.POLICY_ID)).map(UUID::fromString),
                policyDetails.getString(Constants.USER_EMAIL_ID),
                UUID.fromString(policyDetails.getString(Constants.ITEM_ID)),
                UUID.fromString(policyDetails.getString(Constants.OWNER_ID)),
                policyDetails.getString(Constants.STATUS),
                policyDetails.getString(Constants.EXPIRY_AT),
                Optional.ofNullable(policyDetails.getString(Constants.CREATED_AT)),
                Optional.ofNullable(policyDetails.getString(Constants.UPDATED_AT)),
                policyDetails.getJsonObject(Constants.CONSTRAINTS, new JsonObject())
        );
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put(Constants.POLICY_ID, policyId.map(UUID::toString).orElse(null))
                .put(Constants.USER_EMAIL_ID, userEmailId)
                .put(Constants.ITEM_ID, itemId.toString())
                .put(Constants.OWNER_ID, ownerId.toString())
                .put(Constants.STATUS, status)
                .put(Constants.EXPIRY_AT, expiryAt)
                .put(Constants.CREATED_AT, createdAt.orElse(null))
                .put(Constants.UPDATED_AT, updatedAt.orElse(null))
                .put(Constants.CONSTRAINTS, constraints);
    }

    public Map<String, Object> toNonEmptyFieldsMap() {
        Map<String, Object> map = new HashMap<>();
        policyId.ifPresent(id -> map.put(Constants.POLICY_ID, id.toString()));
        if (userEmailId != null) map.put(Constants.USER_EMAIL_ID, userEmailId);
        if (itemId != null) map.put(Constants.ITEM_ID, itemId.toString());
        if (ownerId != null) map.put(Constants.OWNER_ID, ownerId.toString());
        if (status != null) map.put(Constants.STATUS, status);
        if (expiryAt != null) map.put(Constants.EXPIRY_AT, expiryAt);
        createdAt.ifPresent(value -> map.put(Constants.CREATED_AT, value));
        updatedAt.ifPresent(value -> map.put(Constants.UPDATED_AT, value));
        if (constraints != null && !constraints.isEmpty()) map.put(Constants.CONSTRAINTS, constraints);
        return map;
    }

}
