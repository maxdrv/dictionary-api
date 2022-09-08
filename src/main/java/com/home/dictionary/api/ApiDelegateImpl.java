package com.home.dictionary.api;

import com.home.dictionary.facade.LessonFacade;
import com.home.dictionary.facade.PhraseFacade;
import com.home.dictionary.facade.PlanFacade;
import com.home.dictionary.facade.TagFacade;
import com.home.dictionary.openapi.api.ApiApiDelegate;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.util.PageableBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiDelegateImpl implements ApiApiDelegate {

    private final LessonFacade lessonFacade;
    private final PlanFacade planFacade;
    private final TagFacade tagFacade;
    private final PhraseFacade phraseFacade;

    @Override
    public ResponseEntity<PageOfPhraseDto> getPhrases(Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        return ResponseEntity.ok(phraseFacade.getPage(pageable));
    }

    @Override
    public ResponseEntity<PhraseDto> getPhraseById(Long phraseId) {
        return ResponseEntity.ok(phraseFacade.getPhraseByIdOrThrow(phraseId));
    }

    @Override
    public ResponseEntity<PhraseDto> createPhrase(CreatePhraseRequest createPhraseRequest) {
        return new ResponseEntity<>(phraseFacade.createPhrase(createPhraseRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PhraseDto> updatePhrase(Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        return ResponseEntity.ok(phraseFacade.updatePhraseById(phraseId, updatePhraseRequest));
    }

    @Override
    public ResponseEntity<Void> deletePhraseById(Long phraseId) {
        phraseFacade.deletePhraseById(phraseId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PageOfTagDto> getTags(Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        return ResponseEntity.ok(tagFacade.page(pageable));
    }

    @Override
    public ResponseEntity<TagDto> getTagByKey(String tagKey) {
        return ResponseEntity.ok(tagFacade.getTagByKeyOrThrow(tagKey));
    }

    @Override
    public ResponseEntity<TagDto> createTag(CreateTagRequest createTagRequest) {
        return new ResponseEntity<>(tagFacade.createTag(createTagRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TagDto> updateTagByKey(String tagKey, UpdateTagRequest updateTagRequest) {
        return ResponseEntity.ok(tagFacade.updateTagByKey(tagKey, updateTagRequest));
    }

    @Override
    public ResponseEntity<Void> deleteTagByKey(String tagKey) {
        tagFacade.deleteTagByKey(tagKey);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PageOfPlanGridDto> getPlans(
            String description,
            List<String> tags,
            Integer page,
            Integer size,
            String sort
    ) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        return ResponseEntity.ok(planFacade.getPlanGridPage(description, tags, pageable));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> getPlanById(Long planId) {
        return ResponseEntity.ok(planFacade.getPlanByIdOrThrow(planId));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> createPlan(CreatePlanRequest createPlanRequest) {
        return new ResponseEntity<>(planFacade.createPlan(createPlanRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PlanDetailedDto> updatePlanById(Long planId, UpdatePlanRequest updatePlanRequest) {
        return ResponseEntity.ok(planFacade.updatePlanById(planId, updatePlanRequest));
    }

    @Override
    public ResponseEntity<Void> deletePlanById(Long planId) {
        planFacade.deletePlanById(planId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ListOfTagDto> getTagsByPlanId(Long planId) {
        return ResponseEntity.ok(tagFacade.getTagsByPlanId(planId));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> addTagToPlan(Long planId, CreateTagRequest createTagRequest) {
        return new ResponseEntity<>(planFacade.addTagToPlan(planId, createTagRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PlanDetailedDto> removeTagFromPlan(Long planId, String tagKey) {
        return ResponseEntity.ok(planFacade.removeTagFromPlan(planId, tagKey));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> addPhraseToPlan(Long planId, CreatePhraseRequest createPhraseRequest) {
        return ResponseEntity.ok(planFacade.addPhraseToPlan(planId, createPhraseRequest));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> updatePhraseInPlan(Long planId, Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        return ResponseEntity.ok(planFacade.updatePhraseInPlan(planId, phraseId, updatePhraseRequest));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> removePhraseFromPlan(Long planId, Long phraseId) {
        return ResponseEntity.ok(planFacade.removePhraseFromPlan(planId, phraseId));
    }

    @Override
    public ResponseEntity<PageOfLessonDto> getLessons(Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        return ResponseEntity.ok(lessonFacade.getPage(pageable));
    }

    @Override
    public ResponseEntity<NextQuestionDto> startLessonFromPlan(Long planId, @Nullable OrderStrategyTypeDto orderStrategyType) {
        var orderStrategyTypeDto = Optional.ofNullable(orderStrategyType).orElse(OrderStrategyTypeDto.NATURAL);
        var nextQuestionDto = lessonFacade.startLessonFromPlan(planId, orderStrategyTypeDto);
        return ResponseEntity.ok(nextQuestionDto);
    }

    @Override
    public ResponseEntity<LessonDto> getLessonById(Long lessonId) {
        return ResponseEntity.ok(lessonFacade.getLessonById(lessonId));
    }

    @Override
    public ResponseEntity<NextQuestionDto> getNextQuestionByLessonId(Long lessonId) {
        return ResponseEntity.ok(lessonFacade.getNextQuestionByLessonId(lessonId));
    }

    @Override
    public ResponseEntity<NextQuestionDto> answerTheQuestion(Long lessonId, Long lessonItemId, AnswerDto answerDto) {
        return ResponseEntity.ok(lessonFacade.answerTheQuestion(lessonId, lessonItemId, answerDto));
    }

}
