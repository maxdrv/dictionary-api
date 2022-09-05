package com.home.dictionary.service;


import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.tag.Tag;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final PhraseService phraseService;
    private final TagService tagService;
    private final EntityManager entityManager;

    public Page<Lesson> getPage(Pageable pageable) {
        return lessonRepository.findAll(pageable);
    }

    public Optional<Lesson> getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId);
    }

    public Lesson getLessonByIdOrThrow(Long lessonId) {
        return getLessonById(lessonId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Lesson with id " + lessonId + " not found"));
    }

    public Lesson createLesson(CreateLessonRequest request) {
        var listOfTags = tagService.createTags(request.getTags());

        var toSave = new Lesson();
        toSave.setDescription(request.getDescription());
        toSave.setTags(new HashSet<>(listOfTags));

        return lessonRepository.save(toSave);
    }

    public Lesson updateLesson(Long lessonId, UpdateLessonRequest request) {
        var toUpdated = getLessonByIdOrThrow(lessonId);
        toUpdated.setDescription(request.getDescription());
        return lessonRepository.save(toUpdated);
    }

    public void deleteById(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    public void addTagToLesson(Long lessonId, CreateTagRequest createTagRequest) {
        var tag = tagService.createTag(createTagRequest);

        var toUpdated = getLessonByIdOrThrow(lessonId);
        toUpdated.getTags().add(tag);

        entityManager.flush();
    }

    public void removeTagFromLesson(Long lessonId, String tagKey) {
        var toUpdated = getLessonByIdOrThrow(lessonId);

        var newSetOfTags = StreamEx.of(toUpdated.getTags())
                .removeBy(Tag::getKey, tagKey)
                .toSet();

        toUpdated.setTags(newSetOfTags);

        entityManager.flush();
    }

    public Lesson addPhraseToLesson(Long lessonId, CreatePhraseRequest createPhraseRequest) {
        var lesson = getLessonByIdOrThrow(lessonId);

        var newPhrase = phraseService.create(createPhraseRequest);
        newPhrase.setLesson(lesson);

        return lessonRepository.save(lesson);
    }

    public Lesson updatePhraseInLesson(Long lessonId, Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var lesson = getLessonByIdOrThrow(lessonId);

        var maybePhrase = StreamEx.of(lesson.getPhrases())
                .findFirst(phrase -> Objects.equals(phrase.getId(), phraseId));

        if (maybePhrase.isPresent()) {
            var phraseToUpdate = maybePhrase.get();
            phraseService.update(phraseToUpdate.getId(), updatePhraseRequest);
        }

        return getLessonByIdOrThrow(lesson.getId());
    }

    public Lesson removePhraseFromLesson(Long lessonId, Long phraseId) {
        var lesson = getLessonByIdOrThrow(lessonId);

        var maybePhrase = StreamEx.of(lesson.getPhrases())
                .findFirst(phrase -> Objects.equals(phrase.getId(), phraseId));

        if (maybePhrase.isPresent()) {
            var phraseToDelete = maybePhrase.get();
            phraseToDelete.setLesson(null);
            phraseService.deleteById(phraseToDelete.getId());
        }

        return getLessonByIdOrThrow(lesson.getId());
    }

}
