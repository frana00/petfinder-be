package com.petsignal.user;

import com.petsignal.user.dto.UserRequest;
import com.petsignal.user.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRequest request);
    
    UserResponseDto toResponseDto(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
} 