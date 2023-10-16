package com.arthurfrei.security.config;

import com.arthurfrei.security.jwt.JwtConfig;
import com.arthurfrei.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class Config {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String CREATE_USER_V1 = "/api/v1/users/register";

    private static final String ADMIN_ENDPOINT_V1 = "/api/v1/admin/**";

    private static final String LOGIN_ENDPOINT_V1 = "/api/v1/auth/login";

    private static final String ADMIN = "ADMIN";

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(CREATE_USER_V1).permitAll()
                        .requestMatchers(LOGIN_ENDPOINT_V1).permitAll()
                        .requestMatchers(ADMIN_ENDPOINT_V1).hasRole(ADMIN)
                        .anyRequest().authenticated()
                )
                .apply(new JwtConfig(jwtTokenProvider));

        return http.build();
    }


}
