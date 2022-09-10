package com.home.dictionary.facade;

import com.home.dictionary.mapper.PhraseMapper;
import com.home.dictionary.model.phrase.PhraseFilter;
import com.home.dictionary.openapi.model.CreatePhraseRequest;
import com.home.dictionary.openapi.model.PageOfPhraseDto;
import com.home.dictionary.openapi.model.PhraseDto;
import com.home.dictionary.openapi.model.UpdatePhraseRequest;
import com.home.dictionary.service.PhraseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional
public class PhraseFacade {

    private final PhraseService phraseService;
    private final PhraseMapper phraseMapper;

    public PageOfPhraseDto getPage(PhraseFilter filter, Pageable pageable) {
        var result = phraseService.getPage(filter, pageable);
        return new PageOfPhraseDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(phraseMapper::map).toList());
    }

    public PhraseDto getPhraseByIdOrThrow(Long phraseId) {
        var entity = phraseService.getPhraseByIdOrThrow(phraseId);
        return phraseMapper.map(entity);
    }

    public PhraseDto createPhrase(CreatePhraseRequest createPhraseRequest) {
        var entity = phraseService.create(createPhraseRequest);
        return phraseMapper.map(entity);
    }

    public PhraseDto updatePhraseById(Long phraseId, UpdatePhraseRequest updatePhraseRequest) {
        var entity = phraseService.update(phraseId, updatePhraseRequest);
        return phraseMapper.map(entity);
    }

    public void deletePhraseById(Long phraseId) {
        phraseService.deleteById(phraseId);
    }

}
