package com.home.dictionary.service;

import com.home.dictionary.exception.ApiSecurityException;
import com.home.dictionary.exception.NotAllowedException;
import com.home.dictionary.mapper.ApiUserMapper;
import com.home.dictionary.model.user.ApiUser;
import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.openapi.model.LoginRequest;
import com.home.dictionary.openapi.model.RegisterRequest;
import com.home.dictionary.repository.ApiUserRepository;
import com.home.dictionary.repository.AuthorityRepository;
import com.home.dictionary.security.JwtProvider;
import com.home.dictionary.service.auth.LoginResponse;
import com.home.dictionary.service.auth.RefreshResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Collections;

@RequiredArgsConstructor

@Service
@Transactional
public class AuthService {

    private final ConfigurationService configurationService;
    private final ApiUserRepository apiUserRepository;
    private final AuthorityRepository authorityRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final Clock clock;
    private final ApiUserMapper apiUserMapper;

    @Transactional
    public void register(RegisterRequest request) {
        if (configurationService.registrationNotAllowed()) {
            throw new NotAllowedException("Sorry! But we temporarily do not accept registration requests (((");
        }

        ApiUser apiUser = new ApiUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                false,
                Collections.emptyList(),
                clock.instant()
        );
        var saved = apiUserRepository.save(apiUser);
        Authority authority = new Authority(AuthorityType.USER, saved);
        authorityRepository.save(authority);
    }

    @Transactional(readOnly = true)
    public ApiUser getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return apiUserRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + user.getUsername()));
        } else {
            throw new UsernameNotFoundException("Can not identify user");
        }
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication userPassword = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(userPassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = apiUserRepository.findByUsernameOrThrow(request.getUsername());
        String accessToken = jwtProvider.generateAccessToken(request.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(request.getUsername());
        user.setRefreshToken(refreshToken);

        return new LoginResponse(
                user.getUsername(),
                apiUserMapper.mapToRoleDtoList(user.getAuthorities()),
                accessToken,
                refreshToken
        );
    }

    @Transactional(readOnly = true)
    public RefreshResponse refreshToken(String refreshToken) {
        var foundedUser = apiUserRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ApiSecurityException("token not founded"));

        boolean valid = jwtProvider.validateToken(refreshToken);  // TODO: проверить, если токен истек, то валидации не будет
        if (!valid) {
            throw new ApiSecurityException("refresh token invalid");
        }

        String accessToken = jwtProvider.generateAccessToken(foundedUser.getUsername());
        return new RefreshResponse(
                foundedUser.getUsername(),
                apiUserMapper.mapToRoleDtoList(foundedUser.getAuthorities()),
                accessToken
        );
    }

    @Transactional
    public void logout(String refreshToken) {
        apiUserRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> user.setRefreshToken(null));
    }

}
