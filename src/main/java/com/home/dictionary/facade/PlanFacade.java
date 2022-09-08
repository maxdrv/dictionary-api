package com.home.dictionary.facade;

import com.home.dictionary.mapper.PlanMapper;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

@Service
@Transactional
public class PlanFacade {

    private final PlanService planService;
    private final PlanMapper planMapper;

    public PageOfPlanGridDto getPlanGridPage(
            String description,
            List<String> tags,
            Pageable pageable
    ) {
        var result = planService.getPage(pageable);
        return new PageOfPlanGridDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(planMapper::toGridDto).toList());
    }

    public PlanDetailedDto getPlanByIdOrThrow(Long planId) {
        var entity = planService.getPlanByIdOrThrow(planId);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto createPlan(CreatePlanRequest createPlanRequest) {
        var entity = planService.createPlan(createPlanRequest);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto updatePlanById(Long planId, UpdatePlanRequest updatePlanRequest) {
        var entity = planService.updatePlan(planId, updatePlanRequest);
        return planMapper.toDetailedDto(entity);
    }

    public void deletePlanById(Long planId) {
        planService.deleteById(planId);
    }

    public PlanDetailedDto addTagToPlan(Long planId, CreateTagRequest createTagRequest) {
        var entity = planService.addTagToPlan(planId, createTagRequest);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto removeTagFromPlan(Long planId, String tagKey) {
        var entity = planService.removeTagFromPlan(planId, tagKey);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto addPhraseToPlan(Long planId, CreatePhraseRequest createPhraseRequest) {
        var entity = planService.addPhraseToPlan(planId, createPhraseRequest);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto updatePhraseInPlan(Long planId, Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var entity = planService.updatePhraseInPlan(planId, phraseId, updatePhraseRequest);
        return planMapper.toDetailedDto(entity);
    }

    public PlanDetailedDto removePhraseFromPlan(Long planId, Long phraseId) {
        var entity = planService.removePhraseFromPlan(planId, phraseId);
        return planMapper.toDetailedDto(entity);
    }

}
