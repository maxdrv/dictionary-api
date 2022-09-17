package com.home.dictionary.controller;

import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.AuthService;
import com.home.dictionary.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public AuthenticationResponse login(@Validated @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/api/v1/auth/refresh/token")
    public AuthenticationResponse refreshToken(@Validated @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<MessageDto> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(OK).body(new MessageDto().message("Refresh Token Deleted Successfully!"));
    }

}
