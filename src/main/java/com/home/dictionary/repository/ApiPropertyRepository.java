package com.home.dictionary.repository;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.configuration.ApiProperty;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApiPropertyRepository extends CrudRepository<ApiProperty, Long> {

    Optional<ApiProperty> findByKey(String key);

    default ApiProperty findByKeyOrThrow(String key) {
        return findByKey(key)
                .orElseThrow(() -> new ApiEntityNotFoundException("Property with key " + key + " not found"));
    }

}
