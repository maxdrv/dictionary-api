package com.home.dictionary.mapper;

import com.home.dictionary.model.demo.Demo;
import com.home.dictionary.model.demo.DemoType;
import com.home.dictionary.openapi.model.DemoDto;
import com.home.dictionary.openapi.model.DemoTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DemoMapper {

    DemoDto map(Demo entity);

    DemoType fromDto(DemoTypeDto typeDto);

    DemoTypeDto toDto(DemoType type);

}
