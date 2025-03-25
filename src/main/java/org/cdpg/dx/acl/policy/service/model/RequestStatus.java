package org.cdpg.dx.acl.policy.service.model;

import static iudx.apd.acl.server.common.ResponseUrn.RESOURCE_NOT_FOUND_URN;

import iudx.apd.acl.server.common.DxRuntimeException;
import java.util.stream.Stream;

public enum RequestStatus {
  REJECTED("rejected"),
  PENDING("pending"),
  GRANTED("granted"),
  WITHDRAWN("withdrawn");
  private final String status;

  RequestStatus(String value) {
    status = value;
  }

  public static RequestStatus fromString(String requestStatus) {
    return Stream.of(values())
        .filter(element -> element.getRequestStatus().equalsIgnoreCase(requestStatus))
        .findAny()
        .orElseThrow(() -> new DxRuntimeException(404, RESOURCE_NOT_FOUND_URN));
  }

  public String getRequestStatus() {
    return status;
  }
}
