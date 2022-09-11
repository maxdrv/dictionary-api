package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.mapper.LangMapper;
import com.home.dictionary.model.phrase.Phrase;
import com.home.dictionary.model.phrase.PhraseFilter;
import com.home.dictionary.model.phrase.PhraseSpecification;
import com.home.dictionary.openapi.model.CreatePhraseRequest;
import com.home.dictionary.openapi.model.UpdatePhraseRequest;
import com.home.dictionary.repository.PhraseRepository;
import com.home.dictionary.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional
public class PhraseService {

    private final PhraseRepository phraseRepository;
    private final PlanRepository planRepository;
    private final LangMapper langMapper;
    private final EntityManager entityManager;

    public Page<Phrase> getPage(PhraseFilter filter, Pageable pageable) {
        return phraseRepository.findAll(new PhraseSpecification(filter), pageable);
    }

    public Optional<Phrase> getPhraseById(Long phraseId) {
        return phraseRepository.findById(phraseId);
    }

    public Phrase getPhraseByIdOrThrow(Long phraseId) {
        return getPhraseById(phraseId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Phrase with id " + phraseId + " not found"));
    }

    public Phrase create(CreatePhraseRequest request) {
        var plan = Optional.ofNullable(request.getPlanId())
                .map(planRepository::findByIdOrThrow)
                .orElse(null);

        var toSave = new Phrase();
        toSave.setPlan(plan);
        toSave.setSource(request.getSource());
        toSave.setSourceLang(langMapper.map(request.getSourceLang()));
        toSave.setTranscription(request.getTranscription());
        toSave.setTarget(request.getTarget());
        toSave.setTargetLang(langMapper.map(request.getTargetLang()));
        return phraseRepository.save(toSave);
    }

    public Phrase update(Long phraseId, UpdatePhraseRequest request) {
        var toUpdate = getPhraseByIdOrThrow(phraseId);
        toUpdate.setSource(request.getSource());
        toUpdate.setSourceLang(langMapper.map(request.getSourceLang()));
        toUpdate.setTranscription(request.getTranscription());
        toUpdate.setTarget(request.getTarget());
        toUpdate.setTargetLang(langMapper.map(request.getTargetLang()));
        entityManager.flush();
        return getPhraseByIdOrThrow(toUpdate.getId());
    }

    public void deleteById(Long phraseId) {
        getPhraseById(phraseId)
                .ifPresent(phrase -> {
                    phrase.setPlan(null);
                    phraseRepository.save(phrase);
                });

        phraseRepository.deleteById(phraseId);
    }

}
