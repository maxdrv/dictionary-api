package com.home.dictionary.repository;

import com.home.dictionary.model.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LessonRepository extends PagingAndSortingRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

}
