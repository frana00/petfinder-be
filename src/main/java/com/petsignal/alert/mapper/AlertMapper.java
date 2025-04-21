package com.petsignal.alert.mapper;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AlertMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
  @Mapping(target = "postCode", source = ".", qualifiedByName = "requestToPostCode")
  Alert toEntity(AlertRequest request);

  @Mapping(target = "photoUrls", ignore = true)
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "postalCode", source = "postCode.postalCode")
  @Mapping(target = "countryCode", source = "postCode.countryCode")
  AlertResponse toResponse(Alert alert);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "photos", ignore = true)
  @Mapping(target = "user", source = "userId", qualifiedByName = "userIdToUser")
  @Mapping(target = "postCode", source = ".", qualifiedByName = "requestToPostCode")
  void updateEntityFromRequest(AlertRequest request, @MappingTarget Alert alert);

  @Named("userIdToUser")
  default User userIdToUser(Long userId) {
    if (userId == null) {
      return null;
    }
    User user = new User();
    user.setId(userId);
    return user;
  }

  @Named("requestToPostCode")
  default PostCode requestToPostCode(AlertRequest request) {
    if (request.getPostalCode() == null && request.getCountryCode() == null) {
      return null;
    }
    PostCode postCode = new PostCode();
    postCode.setPostalCode(request.getPostalCode());
    postCode.setCountryCode(request.getCountryCode());
    return postCode;
  }
} 