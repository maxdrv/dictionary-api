package com.home.dictionary.facade;

import com.home.dictionary.mapper.LessonMapper;
import com.home.dictionary.mapper.OrderStrategyMapper;
import com.home.dictionary.model.lesson.LessonItem;
import com.home.dictionary.model.lesson.LessonItemStatus;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.LessonService;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional
public class LessonFacade {

    private final LessonService lessonService;
    private final LessonMapper lessonMapper;
    private final OrderStrategyMapper orderStrategyTypeMapper;

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

    public NextQuestionDto answerTheQuestion(Long lessonId, AnswerDto answer) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);

        // TODO

        return nextQuestion(lesson.getId());
    }

    public NextQuestionDto nextQuestion(Long lessonId) {
        var lesson = lessonService.getLessonByIdOrThrow(lessonId);

        var lessonItem = StreamEx.of(lesson.getLessonItems())
                .sortedBy(LessonItem::getItemOrder)
                .findFirst(item -> item.getStatus() == LessonItemStatus.NOT_STARTED)
                .orElse(null);

        var nextQuestion = new NextQuestionDto()
                .lessonId(lesson.getId())
                .hasQuestion(lessonItem != null);

        if (lessonItem != null) {
            var questionDto = new QuestionDto()
                    .lessonItemId(lessonItem.getId())
                    .question(lessonItem.getQuestion());
            nextQuestion.question(questionDto);
        }

        return nextQuestion;
    }

}
