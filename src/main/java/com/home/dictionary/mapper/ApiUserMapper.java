package com.home.dictionary.mapper;

import com.home.dictionary.model.user.ApiUser;
import com.home.dictionary.openapi.model.ApiUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface ApiUserMapper {

    ApiUserDto map(ApiUser entity);

}
