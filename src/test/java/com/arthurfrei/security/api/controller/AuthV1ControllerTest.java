package com.arthurfrei.security.api.controller;

import com.arthurfrei.security.api.dto.AuthenticationDto;
import com.arthurfrei.security.jwt.JwtTokenProvider;
import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.Role;
import com.arthurfrei.security.store.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthV1ControllerTest {

    @Test
    @DisplayName("Получение jwt токена по существующей учетки")
    void login_Ok() {
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UserService userService = mock(UserService.class);

        AuthV1Controller authV1Controller = new AuthV1Controller(authManager, jwtTokenProvider, userService);

        String displayName = "displayName";
        String username = "username";
        String password = "password";
        String accessToken = "accessToken";
        String token = "token";

        Set<Role> roles = Set.of(new Role("ROLE_USER", null));
        User user = new User(username, "firstName", "lastName", "email", password, roles);

        Map<String, Object> map = Map.of(displayName, username, accessToken, token);

        when(userService.findByUsername(username)).thenReturn(user);
        when(jwtTokenProvider.createToken(username, roles)).thenReturn(token);

        AuthenticationDto authenticationDto = new AuthenticationDto(username, password);

        ResponseEntity<Map<String, Object>> response = authV1Controller.login(authenticationDto);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Map<String, Object> body = response.getBody();
        assert body != null;
        assertEquals(body.get(displayName), username);
        assertEquals(body.get(accessToken), token);
    }

    @Test
    @DisplayName("Попытка получения jwt токена")
    void login_BadCredentials() {
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UserService userService = mock(UserService.class);

        AuthV1Controller authV1Controller = new AuthV1Controller(authManager, jwtTokenProvider, userService);

        BadCredentialsException _throw = assertThrows(
                BadCredentialsException.class,
                () -> authV1Controller.login(null));

        assertEquals(_throw.getMessage(), "Invalid username or password");
    }
}