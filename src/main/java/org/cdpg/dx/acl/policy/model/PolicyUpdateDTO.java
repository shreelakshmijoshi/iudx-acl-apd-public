
package org.cdpg.dx.acl.policy.model;
import java.util.Optional;

import io.vertx.core.json.JsonObject;
import org.cdpg.dx.acl.policy.util.Constants;
import java.util.Map;
import java.util.HashMap;

public record PolicyUpdateDTO(
        Optional<String> status,
        Optional<String> expiryAt,
        Optional<JsonObject> constraints
) {
    public Map<String, Object> toNonEmptyFieldsMap() {
        Map<String, Object> map = new HashMap<>();
        status.ifPresent(value -> map.put(Constants.STATUS, value));
        expiryAt.ifPresent(value -> map.put(Constants.EXPIRY_AT, value));
        constraints.ifPresent(value ->  map.put(Constants.CONSTRAINTS, value));
        return map;
    }
}
