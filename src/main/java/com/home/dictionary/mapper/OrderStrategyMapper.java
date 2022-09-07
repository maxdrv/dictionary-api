package com.home.dictionary.mapper;

import com.home.dictionary.openapi.model.OrderStrategyTypeDto;
import com.home.dictionary.service.order.OrderStrategyType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface OrderStrategyMapper {

    OrderStrategyTypeDto map(OrderStrategyType strategy);

    OrderStrategyType map(OrderStrategyTypeDto strategy);

}
