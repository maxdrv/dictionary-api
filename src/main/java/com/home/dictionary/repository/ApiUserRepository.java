package com.home.dictionary.repository;

import com.home.dictionary.model.user.ApiUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ApiUserRepository extends PagingAndSortingRepository<ApiUser, Long>, JpaSpecificationExecutor<ApiUser> {

    Optional<ApiUser> findByUsername(String username);

}
