package com.home.dictionary.mapper;

import com.home.dictionary.model.plan.Plan;
import com.home.dictionary.openapi.model.PlanDetailedDto;
import com.home.dictionary.openapi.model.PlanGridDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {
                DateTimeMapper.class,
                PhraseMapper.class
        }
)
public interface PlanMapper {

    PlanDetailedDto toDetailedDto(Plan entity);

    PlanGridDto toGridDto(Plan entity);

}
