package org.cdpg.dx.auth.authorization.handler;


import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.cdpg.dx.auth.authorization.exception.AuthorizationException;
import org.cdpg.dx.common.models.DxRole;
import org.cdpg.dx.common.models.JwtData;
import org.cdpg.dx.util.RoutingContextHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

        import io.vertx.core.json.JsonObject;
import java.util.Optional;

class AuthorizationHandlerTest {

    private AuthorizationHandler authorizationHandler;
    private RoutingContext mockContext;

    @BeforeEach
    void setUp() {
        authorizationHandler = new AuthorizationHandler();
        mockContext = mock(RoutingContext.class);
    }

    @Test
    void testAuthorizeUser_Success() {
        JwtData validJwt = createJwtData("consumer");
        try (MockedStatic<RoutingContextHelper> mockedStatic = mockStatic(RoutingContextHelper.class)) {
            mockedStatic.when(() -> RoutingContextHelper.getJwtData(mockContext))
                    .thenReturn(Optional.of(validJwt));

            Handler<RoutingContext> handler = authorizationHandler.setUserRolesForEndpoint(DxRole.CONSUMER, DxRole.PROVIDER);
            handler.handle(mockContext);

            verify(mockContext, times(1)).next();
            verify(mockContext, never()).fail(any());
        }
    }

    @Test
    void testAuthorizeUser_Failure_MissingJWT() {
        try (MockedStatic<RoutingContextHelper> mockedStatic = mockStatic(RoutingContextHelper.class)) {
            mockedStatic.when(() -> RoutingContextHelper.getJwtData(mockContext))
                    .thenReturn(Optional.empty());

            Handler<RoutingContext> handler = authorizationHandler.setUserRolesForEndpoint(DxRole.CONSUMER);
            handler.handle(mockContext);

            ArgumentCaptor<AuthorizationException> exceptionCaptor = ArgumentCaptor.forClass(AuthorizationException.class);
            verify(mockContext, times(1)).fail(exceptionCaptor.capture());
            assertEquals("JWT data not found", exceptionCaptor.getValue().getMessage());
        }
    }

    @Test
    void testAuthorizeUser_Failure_InvalidRoleInJWT() {
        JwtData invalidJwt = createJwtData("invalid_role");
        try (MockedStatic<RoutingContextHelper> mockedStatic = mockStatic(RoutingContextHelper.class)) {
            mockedStatic.when(() -> RoutingContextHelper.getJwtData(mockContext))
                    .thenReturn(Optional.of(invalidJwt));

            Handler<RoutingContext> handler = authorizationHandler.setUserRolesForEndpoint(DxRole.CONSUMER);
            handler.handle(mockContext);

            ArgumentCaptor<AuthorizationException> exceptionCaptor = ArgumentCaptor.forClass(AuthorizationException.class);
            verify(mockContext, times(1)).fail(exceptionCaptor.capture());
            assertEquals("Invalid or missing user role", exceptionCaptor.getValue().getMessage());
        }
    }

    @Test
    void testAuthorizeUser_Failure_UnauthorizedRole() {
        JwtData jwtWithUnauthorizedRole = createJwtData("admin");
        try (MockedStatic<RoutingContextHelper> mockedStatic = mockStatic(RoutingContextHelper.class)) {
            mockedStatic.when(() -> RoutingContextHelper.getJwtData(mockContext))
                    .thenReturn(Optional.of(jwtWithUnauthorizedRole));

            Handler<RoutingContext> handler = authorizationHandler.setUserRolesForEndpoint(DxRole.CONSUMER);
            handler.handle(mockContext);

            ArgumentCaptor<AuthorizationException> exceptionCaptor = ArgumentCaptor.forClass(AuthorizationException.class);
            verify(mockContext, times(1)).fail(exceptionCaptor.capture());
            assertEquals("Access denied to endpoint", exceptionCaptor.getValue().getMessage());
        }
    }

    private JwtData createJwtData(String role) {
        JsonObject jwtPayload = new JsonObject()
                .put("access_token", "sampleToken")
                .put("sub", "user123")
                .put("iss", "auth-server")
                .put("aud", "resource-server")
                .put("exp", 1712345678L)
                .put("iat", 1712341234L)
                .put("iid", "instance123")
                .put("role", role)
                .put("drl", "data-role")
                .put("did", "device123")
                .put("expiry", "2025-12-31T23:59:59Z");
        return new JwtData(jwtPayload);
    }
}
