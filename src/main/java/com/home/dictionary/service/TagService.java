package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.model.tag.Tag;
import com.home.dictionary.openapi.model.CreateTagRequest;
import com.home.dictionary.openapi.model.UpdateTagRequest;
import com.home.dictionary.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final EntityManager entityManager;

    public Page<Tag> getPage(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    public List<Tag> getTagsByPlanId(Long planId) {
        return tagRepository.findAllByPlansId(planId);
    }

    public Optional<Tag> getTagByKey(String key) {
        return tagRepository.findByKey(key);
    }

    public Tag getTagByKeyOrThrow(String key) {
        return getTagByKey(key)
                .orElseThrow(() -> new ApiEntityNotFoundException("Tag with key " + key + " not found"));
    }

    public Tag createTag(CreateTagRequest request) {
        var toSave = new Tag();
        toSave.setKey(request.getKey());
        return tagRepository.save(toSave);
    }

    public List<Tag> createTags(List<CreateTagRequest> requests) {
        return requests.stream()
                .map(this::createTag)
                .toList();
    }

    public Tag updateTag(String tagKey, UpdateTagRequest request) {
        var toUpdate = getTagByKeyOrThrow(tagKey);
        toUpdate.setKey(request.getNewKey());
        entityManager.flush();
        return getTagByKeyOrThrow(toUpdate.getKey());
    }

    public void deleteByKey(String key) {
        tagRepository.deleteByKey(key);
    }

}
