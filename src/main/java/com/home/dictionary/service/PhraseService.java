package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.mapper.LangMapper;
import com.home.dictionary.model.phrase.Phrase;
import com.home.dictionary.openapi.model.CreatePhraseRequest;
import com.home.dictionary.openapi.model.UpdatePhraseRequest;
import com.home.dictionary.repository.PhraseRepository;
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
    private final LangMapper langMapper;
    private final EntityManager entityManager;

    public Page<Phrase> getPage(Pageable pageable) {
        return phraseRepository.findAll(pageable);
    }

    public Optional<Phrase> getPageById(Long phraseId) {
        return phraseRepository.findById(phraseId);
    }

    public Phrase getPhraseByIdOrThrow(Long phraseId) {
        return getPageById(phraseId)
                .orElseThrow(() -> new ApiEntityNotFoundException("Phrase with id " + phraseId + " not found"));
    }

    public Phrase create(CreatePhraseRequest request) {
        var toSave = new Phrase();
        toSave.setLesson(null);
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
        phraseRepository.deleteById(phraseId);
    }

}
