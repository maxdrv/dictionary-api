package com.home.dictionary.repository;

import com.home.dictionary.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends PagingAndSortingRepository<Tag, String>, JpaSpecificationExecutor<Tag> {

    Optional<Tag> findByKey(String key);

    void deleteByKey(String key);

    List<Tag> findAllByPlansId(Long planId);

}
