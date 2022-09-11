package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.lesson.LessonItem;
import com.home.dictionary.model.phrase.Phrase;
import com.home.dictionary.model.user.ApiUser;
import com.home.dictionary.repository.LessonItemRepository;
import com.home.dictionary.repository.LessonRepository;
import com.home.dictionary.service.order.OrderStrategies;
import com.home.dictionary.service.order.OrderStrategyType;
import com.home.dictionary.util.Comparators;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Clock;
import java.util.Optional;
import java.util.TreeSet;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonService {

    private final PlanService planService;
    private final LessonRepository lessonRepository;
    private final LessonItemRepository lessonItemRepository;
    private final ApiUserService apiUserService;
    private final EntityManager entityManager;
    private final Clock clock;
    private final OrderStrategies orderStrategies;

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

    public Page<LessonItem> getPageOfLessonItem(Long lessonId, Pageable pageable) {
        return lessonItemRepository.findAllByLessonId(lessonId, pageable);
    }

    public Lesson createLessonFromPlan(Long planId, OrderStrategyType orderStrategyType) {
        var plan = planService.getPlanByIdOrThrow(planId);

        var lesson = new Lesson(clock.instant(), plan.getId(), plan.getDescription());
        var savedLesson = lessonRepository.save(lesson);

        var orderStrategy = orderStrategies.getByType(orderStrategyType);
        TreeSet<Phrase> phrases = new TreeSet<>((o1, o2) -> Comparators.LONG_ASC.compare(o1.getId(), o2.getId()));
        phrases.addAll(plan.getPhrases());
        var iterator = orderStrategy.iterator(phrases);

        var lessonItems = StreamEx.of(iterator)
                .map(phraseAndOrder -> {
                    var phrase = phraseAndOrder.phrase();
                    return new LessonItem(
                            savedLesson,
                            phrase.getId(),
                            phraseAndOrder.order(),
                            "translate phrase [" + phrase.getSource() + "] from " + phrase.getSourceLang() + " to " + phrase.getTargetLang(),
                            phrase.getTarget()
                    );
                })
                .toList();

        savedLesson.setLessonItems(lessonItems);
        entityManager.flush();

        return getLessonByIdOrThrow(savedLesson.getId());
    }

    public Optional<Lesson> getCurrentLesson() {
        return Optional.of(apiUserService.getCurrentUser())
                .map(ApiUser::getCurrentLessonId)
                .flatMap(this::getLessonById);
    }

    public Lesson activateLessonById(Long lessonId) {
        var user = apiUserService.getCurrentUser();
        var lesson = getLessonByIdOrThrow(lessonId);
        user.setCurrentLessonId(lesson.getId());
        entityManager.flush();
        return lesson;
    }

}
