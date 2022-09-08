package com.home.dictionary.facade;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.mapper.LessonMapper;
import com.home.dictionary.mapper.OrderStrategyTypeMapper;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonFacade {

    private final LessonService lessonService;
    private final LessonMapper lessonMapper;
    private final OrderStrategyTypeMapper orderStrategyTypeMapper;
    private final EntityManager entityManager;

    public PageOfLessonDto getPage(Pageable pageable) {
        var result = lessonService.getPage(pageable);
        return new PageOfLessonDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(lessonMapper::map).toList());
    }

    public NextQuestionDto startLessonFromPlan(Long planId, OrderStrategyTypeDto orderStrategyTypeDto) {
        var lesson = lessonService.createLessonFromPlan(planId, orderStrategyTypeMapper.map(orderStrategyTypeDto));
        return nextQuestion(lesson.getId());
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
        return nextQuestion(lesson.getId());
    }

    public NextQuestionDto nextQuestion(Long lessonId) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);

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

}
