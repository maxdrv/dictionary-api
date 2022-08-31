package com.home.dictionary.api;

import com.home.dictionary.mapper.DemoMapper;
import com.home.dictionary.model.demo.DemoFilter;
import com.home.dictionary.openapi.api.ApiApiDelegate;
import com.home.dictionary.openapi.model.*;
import com.home.dictionary.service.DemoService;
import com.home.dictionary.util.PageableBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApiDelegateImpl implements ApiApiDelegate {

    private final DemoService demoService;
    private final DemoMapper demoMapper;

    @Override
    public ResponseEntity<PageOfDemoDto> getDemo(@Nullable String name, @Nullable DemoTypeDto type, Integer page, Integer size, String sort) {
        var pageable = PageableBuilder.of(page, size).sortOrIdAsc(sort).build();
        return ResponseEntity.ok(
                demoService.page(
                        new DemoFilter(
                                name,
                                Optional.ofNullable(type)
                                        .map(demoMapper::fromDto)
                                        .orElse(null)
                        ),
                        pageable
                )
        );
    }

    @Override
    public ResponseEntity<DemoDto> getDemoById(Long demoId) {
        return ResponseEntity.ok(demoService.findById(demoId));
    }

    @Override
    public ResponseEntity<DemoDto> postDemo(CreateDemoDto createDemoDto) {
        return new ResponseEntity<>(demoService.create(createDemoDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<DemoDto> putDemo(Long demoId, UpdateDemoDto updateDemoDto) {
        return ResponseEntity.ok(demoService.update(demoId, updateDemoDto));
    }

    @Override
    public ResponseEntity<Void> deleteDemo(Long demoId) {
        demoService.delete(demoId);
        return ResponseEntity.ok().build();
    }

}
