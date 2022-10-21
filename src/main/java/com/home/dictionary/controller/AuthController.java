package com.home.dictionary.controller;

import com.home.dictionary.exception.ApiSecurityException;
import com.home.dictionary.openapi.model.AuthenticationResponse;
import com.home.dictionary.openapi.model.LoginRequest;
import com.home.dictionary.openapi.model.MessageDto;
import com.home.dictionary.openapi.model.RegisterRequest;
import com.home.dictionary.service.AuthService;
import com.home.dictionary.util.http.CookieBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RequiredArgsConstructor

@RestController
public class AuthController {

    private static final long SEVEN_DAYS_IN_SECONDS = 7 * 24 * 60 * 60;

    private final AuthService authService;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<MessageDto> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity
                .status(CREATED)
                .body(new MessageDto().message("User Registration Successful"));
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthenticationResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        var loginResponse = authService.login(loginRequest);

        var setCookiesHeader = new CookieBuilder()
                .addNode("jwt", loginResponse.refreshToken())
                .addNode("HttpOnly")
                .addNode("Secure")
                .addNode("Max-Age", String.valueOf(SEVEN_DAYS_IN_SECONDS))
                .toHttpHeader();

        HttpHeaders headers = new HttpHeaders();
        headers.add(setCookiesHeader.name(), setCookiesHeader.value());

        return ResponseEntity
                .status(OK)
                .headers(headers)
                .body(
                        new AuthenticationResponse()
                                .username(loginResponse.username())
                                .roles(loginResponse.roles())
                                .accessToken(loginResponse.accessToken())
                );
    }

    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            RequestEntity<Void> request
    ) {
        var cookiesHeaders = Optional.ofNullable(request)
                .map(HttpEntity::getHeaders)
                .map(headers -> headers.get("Cookie"))
                .orElse(List.of());

        var jwtCookie = StreamEx.of(cookiesHeaders)
                .flatMap(header -> HttpCookie.parse(header).stream())
                .filter(cookie -> "jwt".equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        if (jwtCookie == null || jwtCookie.getValue() == null || jwtCookie.getValue().isBlank()) {
            throw new ApiSecurityException("refresh token not provided");
        }
        var refreshToken = jwtCookie.getValue();
        var refreshResponse = authService.refreshToken(refreshToken);
        return ResponseEntity
                .status(OK)
                .body(
                        new AuthenticationResponse()
                                .username(refreshResponse.username())
                                .roles(refreshResponse.roles())
                                .accessToken(refreshResponse.accessToken())
                );
    }

    @GetMapping("/api/v1/auth/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "jwt") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(NO_CONTENT).build();
        }
        authService.logout(refreshToken);

        var setCookiesHeader = new CookieBuilder()
                .addNode("jwt", "")
                .addNode("HttpOnly")
                .addNode("Secure")
                .addNode("Max-Age", "-1")
                .toHttpHeader();

        HttpHeaders headers = new HttpHeaders();
        headers.add(setCookiesHeader.name(), setCookiesHeader.value());

        return ResponseEntity
                .status(NO_CONTENT)
                .headers(headers)
                .build();
    }

}
