package org.cdpg.dx.aaa.client;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AAAWebClientTest {

    private WebClient mockWebClient;
    private AAAWebClient aaaWebClient;
    private HttpResponse<Buffer> mockResponse;
    private HttpRequest<Buffer> mockRequest;
    @BeforeEach
    void setUp() {
        mockWebClient = mock(WebClient.class);
        mockResponse = (HttpResponse<Buffer>) mock(HttpResponse.class);
        mockRequest = (HttpRequest<Buffer>) mock(HttpRequest.class);


        JsonObject config = new JsonObject()
                .put("authHost", "localhost")
                .put("dxAuthBasePath", "/auth")
                .put("clientId", "test-client")
                .put("clientSecret", "test-secret")
                .put("authPort", 8080);

        aaaWebClient = new AAAWebClient(config, mockWebClient);
        when(mockWebClient.get(anyInt(), anyString(), anyString())).thenReturn(mockRequest);
        when(mockRequest.putHeader(anyString(), anyString())).thenReturn(mockRequest);
        when(mockRequest.addQueryParam(anyString(), anyString())).thenReturn(mockRequest);
        when(mockRequest.send()).thenReturn(Future.succeededFuture(mockResponse));
    }

    @Test
    void testFetchUserData_Success() {
        JsonObject mockResponseBody = new JsonObject()
                .put("type", "urn:dx:as:Success")
                .put("results", new JsonObject()
                        .put("userId", "user-id-123")
                        .put("role", "consumer"));

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.bodyAsJsonObject()).thenReturn(mockResponseBody);
        when(mockWebClient.get(anyInt(), anyString(), anyString()).send())
                .thenReturn(Future.succeededFuture(mockResponse));

        Future<JsonObject> future = aaaWebClient.fetchUserData("user-id-123", "consumer", "resource-server");

        assertTrue(future.succeeded());
        assertNotNull(future.result());
        assertEquals("user-id-123", future.result().getString("userId"));
        assertEquals("consumer", future.result().getString("role"));
    }

    @Test
    void testFetchUserData_Failure_InvalidResponse() {
        JsonObject mockResponseBody = new JsonObject().put("type", "urn:dx:as:Failure");

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.bodyAsJsonObject()).thenReturn(mockResponseBody);
        when(mockWebClient.get(anyInt(), anyString(), anyString()).send())
                .thenReturn(Future.succeededFuture(mockResponse));

        Future<JsonObject> future = aaaWebClient.fetchUserData("user-id-123", "consumer", "resource-server");

        assertTrue(future.failed());
        assertEquals("User not present in Auth.", future.cause().getMessage());
    }

    @Test
    void testFetchUserData_Failure_ServerError() {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.statusMessage()).thenReturn("Internal Server Error");
        when(mockWebClient.get(anyInt(), anyString(), anyString()).send())
                .thenReturn(Future.succeededFuture(mockResponse));

        Future<JsonObject> future = aaaWebClient.fetchUserData("user-id-123", "consumer", "resource-server");

        assertTrue(future.failed());
        assertEquals("Auth Server request failed with status: 500", future.cause().getMessage());
    }

    @Test
    void testFetchUserData_Failure_AuthClientError() {
        when(mockWebClient.get(anyInt(), anyString(), anyString()).send())
                .thenReturn(Future.failedFuture("Connection error"));

        Future<JsonObject> future = aaaWebClient.fetchUserData("user-id-123", "consumer", "resource-server");

        assertTrue(future.failed());
        assertEquals("Connection error", future.cause().getMessage());
    }
}
