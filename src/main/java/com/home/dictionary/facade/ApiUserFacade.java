package com.home.dictionary.facade;

import com.home.dictionary.mapper.ApiUserMapper;
import com.home.dictionary.openapi.model.PageOfApiUserDto;
import com.home.dictionary.service.ApiUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor

@Service
@Transactional
public class ApiUserFacade {

    private final ApiUserService apiUserService;
    private final ApiUserMapper apiUserMapper;

    public PageOfApiUserDto getPageOfApiUserDto(Pageable pageable) {
        var result = apiUserService.getPageOfApiUser(pageable);
        return new PageOfApiUserDto()
                .size(result.getSize())
                .number(result.getNumber())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream().map(apiUserMapper::map).toList());
    }
}
