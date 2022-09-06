package com.home.dictionary.service;


import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.plan.Plan;
import com.home.dictionary.model.tag.Tag;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.repository.PlanRepository;
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
public class PlanService {

    private final PlanRepository planRepository;
    private final PhraseService phraseService;
    private final TagService tagService;
    private final EntityManager entityManager;

    public Page<Plan> getPage(Pageable pageable) {
        return planRepository.findAll(pageable);
    }

    public Optional<Plan> getPlanById(Long planId) {
        return planRepository.findById(planId);
    }

    public Plan getPlanByIdOrThrow(Long planId) {
        return getPlanById(planId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Plan with id " + planId + " not found"));
    }

    public Plan createPlan(CreatePlanRequest request) {
        var listOfTags = tagService.createTags(request.getTags());

        var toSave = new Plan();
        toSave.setDescription(request.getDescription());
        toSave.setTags(new HashSet<>(listOfTags));

        return planRepository.save(toSave);
    }

    public Plan updatePlan(Long planId, UpdatePlanRequest request) {
        var toUpdated = getPlanByIdOrThrow(planId);
        toUpdated.setDescription(request.getDescription());
        return planRepository.save(toUpdated);
    }

    public void deleteById(Long planId) {
        planRepository.deleteById(planId);
    }

    public void addTagToPlan(Long planId, CreateTagRequest createTagRequest) {
        var tag = tagService.createTag(createTagRequest);

        var toUpdated = getPlanByIdOrThrow(planId);
        toUpdated.getTags().add(tag);

        entityManager.flush();
    }

    public void removeTagFromPlan(Long planId, String tagKey) {
        var toUpdated = getPlanByIdOrThrow(planId);

        var newSetOfTags = StreamEx.of(toUpdated.getTags())
                .removeBy(Tag::getKey, tagKey)
                .toSet();

        toUpdated.setTags(newSetOfTags);

        entityManager.flush();
    }

    public Plan addPhraseToPlan(Long planId, CreatePhraseRequest createPhraseRequest) {
        var plan = getPlanByIdOrThrow(planId);

        var newPhrase = phraseService.create(createPhraseRequest);
        newPhrase.setPlan(plan);

        return planRepository.save(plan);
    }

    public Plan updatePhraseInPlan(Long planId, Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var plan = getPlanByIdOrThrow(planId);

        var maybePhrase = StreamEx.of(plan.getPhrases())
                .findFirst(phrase -> Objects.equals(phrase.getId(), phraseId));

        if (maybePhrase.isPresent()) {
            var phraseToUpdate = maybePhrase.get();
            phraseService.update(phraseToUpdate.getId(), updatePhraseRequest);
        }

        return getPlanByIdOrThrow(plan.getId());
    }

    public Plan removePhraseFromPlan(Long planId, Long phraseId) {
        var plan = getPlanByIdOrThrow(planId);

        var maybePhrase = StreamEx.of(plan.getPhrases())
                .findFirst(phrase -> Objects.equals(phrase.getId(), phraseId));

        if (maybePhrase.isPresent()) {
            var phraseToDelete = maybePhrase.get();
            phraseToDelete.setPlan(null);
            phraseService.deleteById(phraseToDelete.getId());
        }

        return getPlanByIdOrThrow(plan.getId());
    }

}
