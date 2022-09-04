package com.home.dictionary.mapper;

import com.home.dictionary.model.phrase.Phrase;
import com.home.dictionary.openapi.model.PhraseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface PhraseMapper {

    PhraseDto map(Phrase entity);

}
