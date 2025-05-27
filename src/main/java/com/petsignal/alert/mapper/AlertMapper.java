package com.petsignal.alert.mapper;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AlertMapper {

  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "postCode", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "posts", ignore = true)
  Alert toEntity(AlertRequest request);

  @Mapping(target = "photoUrls", ignore = true)
  @Mapping(target = "postalCode", source = "postCode.postalCode")
  @Mapping(target = "countryCode", source = "postCode.countryCode")
  @Mapping(target = "username", source = "user.username")
  AlertResponse toResponse(Alert alert);

  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "postCode", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "posts", ignore = true)
  void updateEntityFromRequest(AlertRequest request, @MappingTarget Alert alert);

} 