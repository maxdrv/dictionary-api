package com.home.dictionary.mapper;

import com.home.dictionary.model.tag.Tag;
import com.home.dictionary.openapi.model.TagDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {

    TagDto map(Tag tag);

}
