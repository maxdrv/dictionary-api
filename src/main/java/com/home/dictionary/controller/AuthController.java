package com.home.dictionary.controller;

import com.home.dictionary.exception.ApiSecurityException;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.AuthService;
import com.home.dictionary.service.RefreshTokenService;
import com.home.dictionary.util.http.CookieBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
                );
    }

    @GetMapping("/api/v1/auth/refresh")
    public AuthenticationResponse refreshToken(@CookieValue(name = "jwt") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiSecurityException("refresh token not provided");
        }
        var refreshResponse = authService.refreshToken(refreshToken);
        return new AuthenticationResponse()
                .username(refreshResponse.username())
                .roles(refreshResponse.roles())
                .accessToken(refreshResponse.accessToken());
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<MessageDto> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body(new MessageDto().message("Refresh Token Deleted Successfully!"));
    }

}
