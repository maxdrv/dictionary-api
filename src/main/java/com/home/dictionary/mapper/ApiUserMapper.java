package com.home.dictionary.mapper;

import com.home.dictionary.model.user.ApiUser;
import com.home.dictionary.model.user.Authority;
import com.home.dictionary.model.user.AuthorityType;
import com.home.dictionary.openapi.model.ApiUserDto;
import com.home.dictionary.openapi.model.UserRoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = DateTimeMapper.class
)
public interface ApiUserMapper {

    @Mapping(target = "roles", source = "user.authorities")
    ApiUserDto map(ApiUser user);

    UserRoleDto map(AuthorityType authorityType);

    List<AuthorityType> mapToAuthTypeList(List<Authority> authorities);

    List<UserRoleDto> mapToRoleDtoList(List<Authority> authorities);

    default UserRoleDto mapToRoleDto(Authority authority) {
        return map(authority.getType());
    }

    default AuthorityType mapToAuthType(Authority authority) {
        return authority.getType();
    }

}
