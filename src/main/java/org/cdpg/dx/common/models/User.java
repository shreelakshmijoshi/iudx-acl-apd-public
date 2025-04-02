package org.cdpg.dx.common.models;

import static org.cdpg.dx.util.Constants.*;
import static org.cdpg.dx.util.Constants.RS_SERVER_URL;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import org.cdpg.dx.acl.policy.model.Role;

/**
 * User class is used to initialize information about the user like id, role, email-Id etc., <br>
 * User class can store information about Consumer, Provider. <br>
 * If the request is made from Provider Delegate, Consumer delegate, it stores information about the
 * Provider, Consumer whom the user is delegate of
 */
@DataObject(generateConverter = true)
public class User {
    private final String userId;
    private final Role userRole;
    private final String emailId;
    private final String firstName;
    private final String lastName;
    private final String resourceServerUrl;

    public User(JsonObject userDetails) {
        this.userId = userDetails.getString(USER_ID);
        this.userRole = Role.fromString(userDetails.getString(USER_ROLE));
        this.emailId = userDetails.getString(EMAIL_ID);
        this.firstName = userDetails.getString(FIRST_NAME);
        this.lastName = userDetails.getString(LAST_NAME);
        this.resourceServerUrl = userDetails.getString(RS_SERVER_URL);

        /* Converts JsonObject to User class object or dataObject conversion [Deserialization] */
        UserConverter.fromJson(userDetails, this);
    }

    /**
     * Converts Data object or User class object to json object [Serialization]
     *
     * @return JsonObject
     */
    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        UserConverter.toJson(this, jsonObject);
        return jsonObject;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getUserRole() {
        return userRole;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getUserId() {
        return userId;
    }

    public String getResourceServerUrl() {
        return resourceServerUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
       User user = (User) o;
        return Objects.equals(userId, user.userId)
            && userRole == user.userRole
            && Objects.equals(emailId, user.emailId)
            && Objects.equals(firstName, user.firstName)
            && Objects.equals(lastName, user.lastName)
            && Objects.equals(resourceServerUrl, user.resourceServerUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userRole, emailId, firstName, lastName, resourceServerUrl);
    }

    @Override
    public String toString() {
        return "User{"
            + "userId='"
            + userId
            + '\''
            + ", userRole="
            + userRole
            + ", emailId='"
            + emailId
            + '\''
            + ", firstName='"
            + firstName
            + '\''
            + ", lastName='"
            + lastName
            + '\''
            + ", resourceServerUrl='"
            + resourceServerUrl
            + '\''
            + '}';
    }
}

