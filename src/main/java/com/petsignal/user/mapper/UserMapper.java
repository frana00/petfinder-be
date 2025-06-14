package com.petsignal.user.mapper;

import com.petsignal.user.dto.CreateUserRequest;
import com.petsignal.user.dto.UpdateUserRequest;
import com.petsignal.user.dto.UserResponse;
import com.petsignal.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "posts", ignore = true)
  @Mapping(target = "alerts", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", constant = "USER")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "password", ignore = true)
  User toEntity(CreateUserRequest request);

  UserResponse toResponse(User user);

  @Mapping(target = "password", ignore = true)
  @Mapping(target = "posts", ignore = true)
  @Mapping(target = "alerts", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
} 