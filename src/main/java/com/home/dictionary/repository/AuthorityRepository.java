package com.home.dictionary.repository;

import com.home.dictionary.model.user.Authority;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
}
