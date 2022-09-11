package com.home.dictionary.repository;

import com.home.dictionary.model.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface LessonRepository extends PagingAndSortingRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    @Query(
            nativeQuery = true,
            value = """
                    select id from lesson where status = 'STARTED' order by start_at asc limit 1
                    """
    )
    Optional<Long> findOldestStartedLesson();

}
