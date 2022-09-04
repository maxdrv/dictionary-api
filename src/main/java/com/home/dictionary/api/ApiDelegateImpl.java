package com.home.dictionary.api;

import com.home.dictionary.mapper.PhraseMapper;
import com.home.dictionary.mapper.TagMapper;
import com.home.dictionary.openapi.api.ApiApiDelegate;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.PhraseService;
import com.home.dictionary.service.TagService;
import com.home.dictionary.util.PageableBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiDelegateImpl implements ApiApiDelegate {

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
}
