package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.lesson.LessonItem;
import com.home.dictionary.model.lesson.LessonItemStatus;
import com.home.dictionary.model.lesson.LessonStatus;
import com.home.dictionary.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonService {

    private final PlanService planService;
    private final LessonRepository lessonRepository;
    private final EntityManager entityManager;
    private final Clock clock;

    public Optional<Lesson> getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId);
    }

    public Lesson getLessonByIdOrThrow(Long lessonId) {
        return getLessonById(lessonId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Lesson with id " + lessonId + " not found"));
    }

    public Lesson createLessonFromPlan(Long planId) {
        var plan = planService.getPlanByIdOrThrow(planId);

        var lesson = new Lesson();
        lesson.setStartAt(clock.instant());
        lesson.setStatus(LessonStatus.NOT_STARTED);
        lesson.setParentPlanId(plan.getId());
        lesson.setDescription(plan.getDescription());

        var savedLesson = lessonRepository.save(lesson);

        var lessonItems = plan.getPhrases().stream()
                .map(phrase -> {
                    var item = new LessonItem();
                    item.setLesson(savedLesson);
                    item.setStatus(LessonItemStatus.NOT_STARTED);
                    item.setParentPhraseId(phrase.getId());
                    item.setQuestion("translate phrase [" + phrase.getSource() + "] from " + phrase.getSourceLang() + " to " + phrase.getTargetLang());
                    item.setAnswerCorrect(phrase.getTarget());
                    return item;
                })
                .toList();

        savedLesson.setLessonItems(lessonItems);
        entityManager.flush();

        return getLessonByIdOrThrow(savedLesson.getId());
    }

}
