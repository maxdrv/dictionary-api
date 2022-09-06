package com.home.dictionary.api;

import com.home.dictionary.mapper.PlanMapper;
import com.home.dictionary.mapper.PhraseMapper;
import com.home.dictionary.mapper.TagMapper;
import com.home.dictionary.openapi.api.ApiApiDelegate;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.PlanService;
import com.home.dictionary.service.PhraseService;
import com.home.dictionary.service.TagService;
import com.home.dictionary.util.PageableBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiDelegateImpl implements ApiApiDelegate {

    private final PlanService planService;
    private final PlanMapper planMapper;
    private final TagService tagService;
    private final TagMapper tagMapper;
    private final PhraseService phraseService;
    private final PhraseMapper phraseMapper;

    @Override
    public ResponseEntity<PageOfPhraseDto> getPhrases(Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        var result = phraseService.getPage(pageable);
        var pageOfPhrases = new PageOfPhraseDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(phraseMapper::map).toList());
        return ResponseEntity.ok(pageOfPhrases);
    }

    @Override
    public ResponseEntity<PhraseDto> getPhraseById(Long phraseId) {
        var entity = phraseService.getPhraseByIdOrThrow(phraseId);
        return ResponseEntity.ok(phraseMapper.map(entity));
    }

    @Override
    public ResponseEntity<PhraseDto> createPhrase(CreatePhraseRequest createPhraseRequest) {
        var entity = phraseService.create(createPhraseRequest);
        return new ResponseEntity<>(phraseMapper.map(entity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PhraseDto> updatePhrase(Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var entity = phraseService.update(phraseId, updatePhraseRequest);
        return ResponseEntity.ok(phraseMapper.map(entity));
    }

    @Override
    public ResponseEntity<Void> deletePhraseById(Long phraseId) {
        phraseService.deleteById(phraseId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PageOfTagDto> getTags(Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        var result = tagService.getPage(pageable);
        var pageOfTags = new PageOfTagDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(tagMapper::map).toList());
        return ResponseEntity.ok(pageOfTags);
    }

    @Override
    public ResponseEntity<TagDto> getTagByKey(String tagKey) {
        var entity = tagService.getTagByKeyOrThrow(tagKey);
        return ResponseEntity.ok(tagMapper.map(entity));
    }

    @Override
    public ResponseEntity<TagDto> createTag(CreateTagRequest createTagRequest) {
        var entity = tagService.createTag(createTagRequest);
        return new ResponseEntity<>(tagMapper.map(entity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TagDto> updateTagByKey(String tagKey, UpdateTagRequest updateTagRequest) {
        var entity = tagService.updateTag(tagKey, updateTagRequest);
        return ResponseEntity.ok(tagMapper.map(entity));
    }

    @Override
    public ResponseEntity<Void> deleteTagByKey(String tagKey) {
        tagService.deleteByKey(tagKey);
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
        var result = planService.getPage(pageable);
        var pageOfPlanGridDto = new PageOfPlanGridDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(planMapper::toGridDto).toList());
        return ResponseEntity.ok(pageOfPlanGridDto);
    }

    @Override
    public ResponseEntity<PlanDetailedDto> getPlanById(Long planId) {
        var entity = planService.getPlanByIdOrThrow(planId);
        return ResponseEntity.ok(planMapper.toDetailedDto(entity));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> createPlan(CreatePlanRequest createPlanRequest) {
        var entity = planService.createPlan(createPlanRequest);
        return new ResponseEntity<>(planMapper.toDetailedDto(entity), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<PlanDetailedDto> updatePlanById(Long planId, UpdatePlanRequest updatePlanRequest) {
        var entity = planService.updatePlan(planId, updatePlanRequest);
        return ResponseEntity.ok(planMapper.toDetailedDto(entity));
    }

    @Override
    public ResponseEntity<Void> deletePlanById(Long planId) {
        planService.deleteById(planId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ListOfTagDto> getTagsByPlanId(Long planId) {
        var listOfTags = tagService.getTagsByPlanId(planId);
        var listOfTagDto = new ListOfTagDto();
        listOfTagDto.content(listOfTags.stream().map(tagMapper::map).toList());
        return ResponseEntity.ok(listOfTagDto);
    }

    @Override
    public ResponseEntity<ListOfTagDto> addTagToPlan(Long planId, CreateTagRequest createTagRequest) {
        planService.addTagToPlan(planId, createTagRequest);
        return getTagsByPlanId(planId);
    }

    @Override
    public ResponseEntity<ListOfTagDto> removeTagFromPlan(Long planId, String tagKey) {
        planService.removeTagFromPlan(planId, tagKey);
        return getTagsByPlanId(planId);
    }

    @Override
    public ResponseEntity<PlanDetailedDto> addPhraseToPlan(Long planId, CreatePhraseRequest createPhraseRequest) {
        var entity = planService.addPhraseToPlan(planId, createPhraseRequest);
        return ResponseEntity.ok(planMapper.toDetailedDto(entity));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> updatePhraseInPlan(Long planId, Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var entity = planService.updatePhraseInPlan(planId, phraseId, updatePhraseRequest);
        return ResponseEntity.ok(planMapper.toDetailedDto(entity));
    }

    @Override
    public ResponseEntity<PlanDetailedDto> removePhraseFromPlan(Long planId, Long phraseId) {
        var entity = planService.removePhraseFromPlan(planId, phraseId);
        return ResponseEntity.ok(planMapper.toDetailedDto(entity));
    }

}
