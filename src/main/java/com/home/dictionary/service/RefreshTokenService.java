package com.home.dictionary.service;

import com.home.dictionary.exception.ApiSecurityException;
import com.home.dictionary.model.user.RefreshToken;
import com.home.dictionary.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;

@RequiredArgsConstructor

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Clock clock;

    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(), clock.instant());
        return refreshTokenRepository.save(refreshToken);
    }

    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiSecurityException("Invalid refresh token"));
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

}
