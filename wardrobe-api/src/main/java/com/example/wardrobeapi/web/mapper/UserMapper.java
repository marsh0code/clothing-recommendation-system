package com.example.wardrobeapi.web.mapper;

import com.example.wardrobeapi.domain.User;
import com.example.wardrobeapi.web.dto.user.UserCreationDto;
import com.example.wardrobeapi.web.dto.user.UserDto;
import com.example.wardrobeapi.web.dto.user.UserUpdateDto;
import org.mapstruct.*;

@Mapper
public interface UserMapper {
    @Mapping(target = "createdAt", expression = "java(user.getCreatedAt().getEpochSecond())")
    UserDto toPayload(User user);

    User toEntity(UserCreationDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserUpdateDto userDto, @MappingTarget User user);
}
