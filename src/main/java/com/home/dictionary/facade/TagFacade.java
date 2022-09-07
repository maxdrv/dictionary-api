package com.home.dictionary.facade;

import com.home.dictionary.mapper.TagMapper;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional
public class TagFacade {

    private final TagService tagService;
    private final TagMapper tagMapper;

    public PageOfTagDto page(Pageable pageable) {
        var result = tagService.getPage(pageable);
        return new PageOfTagDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(tagMapper::map).toList());
    }

    public TagDto getTagByKeyOrThrow(String tagKey) {
        var entity = tagService.getTagByKeyOrThrow(tagKey);
        return tagMapper.map(entity);
    }

    public ListOfTagDto getTagsByPlanId(Long planId) {
        var listOfTags = tagService.getTagsByPlanId(planId);
        return new ListOfTagDto().content(listOfTags.stream().map(tagMapper::map).toList());
    }

    public TagDto createTag(CreateTagRequest createTagRequest) {
        var entity = tagService.createTag(createTagRequest);
        return tagMapper.map(entity);
    }

    public TagDto updateTagByKey(String tagKey, UpdateTagRequest updateTagRequest) {
        var entity = tagService.updateTag(tagKey, updateTagRequest);
        return tagMapper.map(entity);
    }

    public void deleteTagByKey(String tagKey) {
        tagService.deleteByKey(tagKey);
    }

}
