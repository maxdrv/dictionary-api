package com.home.dictionary.repository;

import com.home.dictionary.model.user.ApiUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface ApiUserRepository extends PagingAndSortingRepository<ApiUser, Long>, JpaSpecificationExecutor<ApiUser> {

    Optional<ApiUser> findByUsername(String username);

    default ApiUser findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with name " + username));
    }

}
