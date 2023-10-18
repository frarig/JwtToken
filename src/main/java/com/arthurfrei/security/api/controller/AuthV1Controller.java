package com.arthurfrei.security.api.controller;

import com.arthurfrei.security.api.dto.AuthenticationDto;
import com.arthurfrei.security.jwt.JwtTokenProvider;
import com.arthurfrei.security.service.UserService;
import com.arthurfrei.security.store.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {

    private final AuthenticationManager manager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthenticationDto authenticationDto) {
        try {
            final String username = authenticationDto.getUsername();
            manager.authenticate(new UsernamePasswordAuthenticationToken(username, authenticationDto.getPassword()));
            final User user = userService.findByUsername(username);

            return ResponseEntity.ok( getResponse(user) );

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private Map<String, Object> getResponse(User user) {
        final String username = user.getUsername();
        final String token = jwtTokenProvider.createToken(username, user.getRoles());

        Map<String, Object> response = new HashMap<>();
        response.put("displayName", username);
        response.put("accessToken", token);

        return response;
    }

}