package com.home.dictionary.repository;

import com.home.dictionary.model.phrase.Phrase;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PhraseRepository extends PagingAndSortingRepository<Phrase, Long>, JpaSpecificationExecutor<Phrase> {

    void deleteByIdAndLessonId(Long id, Long lessonId);

}
