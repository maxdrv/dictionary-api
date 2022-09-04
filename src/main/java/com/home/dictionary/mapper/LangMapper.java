package com.home.dictionary.mapper;

import com.home.dictionary.model.phrase.Lang;
import com.home.dictionary.openapi.model.LangDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LangMapper {

    LangDto map(Lang lang);

    Lang map(LangDto lang);

}
