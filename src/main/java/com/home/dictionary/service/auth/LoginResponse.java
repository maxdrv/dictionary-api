package com.home.dictionary.service.auth;

import com.home.dictionary.openapi.model.UserRoleDto;

import java.time.OffsetDateTime;
import java.util.List;

public record LoginResponse(
        String username,
        List<UserRoleDto> roles,
        String accessToken,
        OffsetDateTime accessExpiresAt,
        String refreshToken,
        OffsetDateTime refreshExpiresAt
) {
}