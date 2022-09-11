package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.user.ApiUser;
import com.home.dictionary.repository.ApiUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional
public class ApiUserService {

    private static final Long STAB_USER_ID = 1L;

    private final ApiUserRepository apiUserRepository;

    public Page<ApiUser> getPageOfApiUser(Pageable pageable) {
        return apiUserRepository.findAll(pageable);
    }

    public Iterable<ApiUser> findAll() {
        return apiUserRepository.findAll();
    }

    public Optional<ApiUser> findById(Long userId) {
        return apiUserRepository.findById(userId);
    }

    public ApiUser findByIdOrThrow(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new ApiEntityNotFoundException("User with id " + userId + " not found"));
    }

    public ApiUser getCurrentUser() {
        return findByIdOrThrow(STAB_USER_ID);
    }

}
