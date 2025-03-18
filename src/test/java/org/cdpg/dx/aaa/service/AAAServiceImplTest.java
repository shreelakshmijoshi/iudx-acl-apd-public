package org.cdpg.dx.aaa.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.cdpg.dx.aaa.client.AAAClient;
import org.cdpg.dx.aaa.models.UserInfo;
import org.cdpg.dx.aaa.util.AAAConstants;
import org.cdpg.dx.common.models.User;
import org.cdpg.dx.common.models.DxRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Unit Tests for AAAServiceImpl
class AAAServiceImplTest {

    private AAAClient mockWebClient;
    private AAAServiceImpl aaaService;

    @BeforeEach
    void setUp() {
        mockWebClient = mock(AAAClient.class);
        mockWebClient = mock(AAAClient.class);
        aaaService = new AAAServiceImpl(mockWebClient);
    }

    @Test
    void testFetchUserInfo_Success() {
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), false, DxRole.CONSUMER, "resource-server");

        JsonObject mockResponse = new JsonObject()
                .put(AAAConstants.USER_ID, userInfo.userId().toString())
                .put(AAAConstants.ROLE, "consumer")
                .put(AAAConstants.EMAIL, "user@example.com")
                .put(AAAConstants.NAME, new JsonObject()
                        .put(AAAConstants.FIRST_NAME, "John")
                        .put(AAAConstants.LAST_NAME, "Doe"))
                .put(AAAConstants.RESOURCE_SERVER, "resource-server");

        when(mockWebClient.fetchUserData(anyString(), anyString(), anyString()))
                .thenReturn(Future.succeededFuture(mockResponse));

        Future<User> futureUser = aaaService.fetchUserInfo(userInfo);

        assertTrue(futureUser.succeeded());
        User user = futureUser.result();
        assertNotNull(user);
        assertEquals(userInfo.userId().toString(), user.userId());
        assertEquals(DxRole.CONSUMER, user.userRole());
        assertEquals("user@example.com", user.emailId());
        assertEquals("John", user.firstName());
        assertEquals("Doe", user.lastName());
        assertEquals("resource-server", user.resourceServerUrl());
    }

    @Test
    void testFetchUserInfo_Failure_InvalidData() {
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), false, DxRole.PROVIDER, "resource-server");

        JsonObject invalidResponse = new JsonObject()
                .put(AAAConstants.USER_ID, userInfo.userId().toString())
                .put(AAAConstants.ROLE, "provider");

        when(mockWebClient.fetchUserData(anyString(), anyString(), anyString()))
                .thenReturn(Future.succeededFuture(invalidResponse));

        Future<User> futureUser = aaaService.fetchUserInfo(userInfo);

        assertTrue(futureUser.failed());
        assertTrue(futureUser.cause().getMessage().contains("Missing required user fields"));
    }

    @Test
    void testFetchUserInfo_Failure_WebClientError() {
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), false, DxRole.CONSUMER, "resource-server");

        when(mockWebClient.fetchUserData(anyString(), anyString(), anyString()))
                .thenReturn(Future.failedFuture("Web client error"));

        Future<User> futureUser = aaaService.fetchUserInfo(userInfo);

        assertTrue(futureUser.failed());
        assertEquals("Unable to retrieve user information.", futureUser.cause().getMessage());
    }

    @Test
    void testFetchUserInfo_Failure_ExceptionDuringMapping() {
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), false, DxRole.CONSUMER, "resource-server");

        JsonObject responseWithInvalidName = new JsonObject()
                .put(AAAConstants.USER_ID, userInfo.userId().toString())
                .put(AAAConstants.ROLE, "admin")
                .put(AAAConstants.EMAIL, "user@example.com")
                .put(AAAConstants.NAME, null)
                .put(AAAConstants.RESOURCE_SERVER, "resource-server");

        when(mockWebClient.fetchUserData(anyString(), anyString(), anyString()))
                .thenReturn(Future.succeededFuture(responseWithInvalidName));

        Future<User> futureUser = aaaService.fetchUserInfo(userInfo);

        assertTrue(futureUser.failed());
        assertTrue(futureUser.cause().getMessage().contains("Invalid user data received"));
    }
}
