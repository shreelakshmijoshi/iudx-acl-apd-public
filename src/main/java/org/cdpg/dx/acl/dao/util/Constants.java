package org.cdpg.dx.acl.dao.util;

import java.util.List;
import java.util.Map;

/**
 * Constants for database
 */
public class Constants {
  public static final String ID = "_id";
  public static final String CONSUMER_EMAIL = "user_emailid";
  public static final String ITEM_ID = "item_id";
  public static final String OWNER_ID = "owner_id";
  public static final String STATUS = "status";
  public static final String EXPIRY_AT = "expiry_at";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String CONSTRAINTS = "constraints";
  public static final String EMAIL_ID = "email_id";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String ADDITIONAL_INFO = "additional_info";
  public static final String USER_ID = "user_id";
  public static final String REQUEST_ID = "request_id";

  //table names
  public static final String POLICY_TABLE = "policy";
  public static final String USER_TABLE = "user_table";
  public static final String NOTIFICATION_TABLE = "request";
  public static final String APPROVED_ACCESS_REQUEST_TABLE = "approved_access_requests";
  public static final String RESOURCE_ENTITY_TABLE = "resource_entity";
  public static final List<String> POLICY_TABLE_COLUMNS =
      List.of(ID, CONSUMER_EMAIL, ITEM_ID,OWNER_ID,STATUS, EXPIRY_AT, CREATED_AT, UPDATED_AT, CONSTRAINTS);
  public static final List<String> USER_TABLE_COLUMNS =
      List.of(ID, EMAIL_ID, FIRST_NAME, LAST_NAME, CREATED_AT, UPDATED_AT);
  public static final List<String> NOTIFICATION_TABLE_COLUMNS =
      List.of(ID, USER_ID, ITEM_ID, OWNER_ID, STATUS, EXPIRY_AT,CREATED_AT, UPDATED_AT,CONSTRAINTS,ADDITIONAL_INFO);
  public static final List<String> APPROVED_ACCESS_REQUEST_TABLE_COLUMNS =
      List.of(ID, REQUEST_ID, "policy_id", CREATED_AT, UPDATED_AT);
  public static final List<String> RESOURCE_ENTITY_TABLE_COLUMNS =
      List.of(ID, "provider_id", "resource_group_id", "item_type", "resource_server_url", CREATED_AT, UPDATED_AT);

}
