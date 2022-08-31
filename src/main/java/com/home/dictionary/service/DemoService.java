package com.home.dictionary.service;

import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.mapper.DemoMapper;
import com.home.dictionary.model.demo.Demo;
import com.home.dictionary.model.demo.DemoFilter;
import com.home.dictionary.model.demo.DemoSpecification;
import com.home.dictionary.openapi.model.CreateDemoDto;
import com.home.dictionary.openapi.model.DemoDto;
import com.home.dictionary.openapi.model.PageOfDemoDto;
import com.home.dictionary.openapi.model.UpdateDemoDto;
import com.home.dictionary.repository.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Service
@Transactional
@RequiredArgsConstructor
public class DemoService {

    private final DemoRepository demoRepository;
    private final DemoMapper demoMapper;
    private final EntityManager entityManager;

    public PageOfDemoDto page(DemoFilter filter, Pageable pageable) {
        var page = demoRepository.findAll(new DemoSpecification(filter), pageable);
        return new PageOfDemoDto()
                .size(page.getSize())
                .number(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent().stream().map(demoMapper::map).toList());
    }

    public DemoDto findById(Long id) {
        var entity = demoRepository.findById(id).orElseThrow(() -> new ApiEntityNotFoundException("demo entity with id " + id + " not found "));
        return demoMapper.map(entity);
    }

    public DemoDto create(CreateDemoDto dto) {
        var entity = new Demo();
        entity.setName(dto.getName());
        entity.setType(demoMapper.fromDto(dto.getType()));
        var saved = demoRepository.save(entity);
        return demoMapper.map(saved);
    }

    public DemoDto update(Long id, UpdateDemoDto dto) {
        var entity = demoRepository.findById(id).orElseThrow(() -> new ApiEntityNotFoundException("demo entity with id " + id + " not found "));
        entity.setName(dto.getName());
        entity.setType(demoMapper.fromDto(dto.getType()));
        entityManager.flush();
        return demoMapper.map(entity);
    }

    public void delete(Long id) {
        demoRepository.deleteById(id);
        entityManager.flush();
    }

}
