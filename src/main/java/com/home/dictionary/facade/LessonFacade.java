package com.home.dictionary.facade;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.mapper.DateTimeMapper;
import com.home.dictionary.mapper.LessonMapper;
import com.home.dictionary.mapper.OrderStrategyTypeMapper;
import com.home.dictionary.model.lesson.Lesson;
import com.home.dictionary.model.lesson.LessonItemStatus;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.ApiUserService;
import com.home.dictionary.service.LessonService;
import com.home.dictionary.util.PageableUtil;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonFacade {

    private final LessonService lessonService;
    private final LessonMapper lessonMapper;
    private final ApiUserService apiUserService;
    private final DateTimeMapper dateTimeMapper;
    private final OrderStrategyTypeMapper orderStrategyTypeMapper;
    private final EntityManager entityManager;

    public PageOfLessonDto getPage(Pageable pageable) {
        var result = lessonService.getPage(pageable);
        return new PageOfLessonDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(toDto(result.getContent()));
    }

    public LessonDto getLessonById(Long lessonId) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);
        return toDto(lesson);
    }

    public PageOfLessonItemDto getPageOfLessonItemDto(Long lessonId) {
        var result = lessonService.getPageOfLessonItem(lessonId, PageableUtil.MAX_SIZE_PAGE);
        return new PageOfLessonItemDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(lessonMapper::map).toList());
    }

    public NextQuestionDto startLessonFromPlan(Long planId, OrderStrategyTypeDto orderStrategyTypeDto) {
        var lesson = lessonService.createLessonFromPlan(planId, orderStrategyTypeMapper.map(orderStrategyTypeDto));
        return getNextQuestionByLessonId(lesson.getId());
    }

    public NextQuestionDto getNextQuestionByLessonId(Long lessonId) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);
        return getNextQuestionByLesson(lesson);
    }

    public static NextQuestionDto getNextQuestionByLesson(Lesson lesson) {
        var maybeLessonItem = lesson.nextItem();

        var nextQuestion = new NextQuestionDto()
                .lessonId(lesson.getId())
                .hasQuestion(maybeLessonItem.isPresent());

        maybeLessonItem.ifPresent(item -> {
            nextQuestion.question(
                    new QuestionDto()
                            .lessonItemId(item.getId())
                            .question(item.getQuestion())
            );
        });

        return nextQuestion;
    }

    public LessonDto activateLessonById(Long lessonId) {
        return toDto(lessonService.activateLessonById(lessonId));
    }

    public NextQuestionDto answerTheQuestion(Long lessonId, Long lessonItemId, AnswerDto answer) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);
        var item = lesson.findItemById(lessonItemId)
                .orElseThrow(() -> new ApiEntityNotFoundException("item with id " + lessonItemId + " not found in lesson " + lessonId));

        lesson.tryToStart();
        item.acceptAnswer(answer.getAnswer());
        if (lesson.nextItem().isEmpty()) {
            lesson.finish();
        }

        entityManager.flush();
        return getNextQuestionByLessonId(lesson.getId());
    }

    public CurrentLessonResponse getCurrentLesson() {
        var maybeLesson = lessonService.getCurrentLesson();
        if (maybeLesson.isEmpty()) {
            return new CurrentLessonResponse().hasLesson(false);
        }
        var lesson = maybeLesson.get();
        var nextQuestion = getNextQuestionByLesson(lesson);

        var finishedItems = StreamEx.of(lesson.getLessonItems())
                .filter(item -> item.getStatus() != LessonItemStatus.NOT_STARTED)
                .map(lessonMapper::map)
                .toList();

        return new CurrentLessonResponse()
                .hasLesson(true)
                .context(
                        new LessonContextDto()
                                .startAt(dateTimeMapper.mapToOffsetAtMoscow(lesson.getStartAt()))
                                .description(lesson.getDescription())
                                .status(lessonMapper.map(lesson.getStatus()))
                                .next(nextQuestion)
                                .lessonId(lesson.getId())
                                .done(finishedItems)
                );
    }

    private List<LessonDto> toDto(List<Lesson> lessons) {
        Long activeLessonId = apiUserService.getCurrentUser().getCurrentLessonId();
        return lessons.stream()
                .map(lesson -> {
                    boolean active = Objects.equals(activeLessonId, lesson.getId());
                    return lessonMapper.map(lesson, active);
                })
                .toList();
    }

    private LessonDto toDto(Lesson lesson) {
        return toDto(List.of(lesson)).stream().findFirst().orElseThrow();
    }

}