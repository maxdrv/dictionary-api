package com.home.dictionary.controller;

import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.AuthService;
import com.home.dictionary.service.RefreshTokenService;
import com.home.dictionary.util.http.CookieBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor

@RestController
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<MessageDto> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return new ResponseEntity<>(new MessageDto().message("User Registration Successful"), OK);
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthenticationResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        var loginResponse = authService.login(loginRequest);

        var sevenDays = 7 * 24 * 60 * 60;
        var setCookiesHeader = new CookieBuilder()
                .addNode("jwt", loginResponse.refreshToken())
                .addNode("Max-Age", String.valueOf(sevenDays))
                .addNode("HttpOnly")
                .addNode("Secure")
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
                                .expiresAt(loginResponse.accessExpiresAt())
                );
    }

    @PostMapping("/api/v1/auth/refresh/token")
    public AuthenticationResponse refreshToken(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) {
        var refreshResponse = authService.refreshToken(refreshTokenRequest);
        return new AuthenticationResponse()
                .username(refreshResponse.username())
                .roles(refreshResponse.roles())
                .accessToken(refreshResponse.accessToken())
                .expiresAt(refreshResponse.accessExpiresAt());
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<MessageDto> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body(new MessageDto().message("Refresh Token Deleted Successfully!"));
    }

}
