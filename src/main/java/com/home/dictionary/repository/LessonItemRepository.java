package com.home.dictionary.repository;

import com.home.dictionary.model.lesson.LessonItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LessonItemRepository extends PagingAndSortingRepository<LessonItem, Long>, JpaSpecificationExecutor<LessonItem> {

    Page<LessonItem> findAllByLessonId(Long lessonId, Pageable pageable);

}
