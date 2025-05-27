package com.petsignal.subscriptions.mapper;

import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.subscriptions.dto.SubscriptionDto;
import com.petsignal.subscriptions.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

  @Mapping(target = "user", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target = "postCodes", ignore = true)
  Subscription toEntity(SubscriptionDto dto);

  @Mapping(target = "userId", source = "subscription.user.id")
  @Mapping(target = "countryCode", source = "subscription.country.countryCode")
  @Mapping(target = "postCodes", qualifiedByName = "postCodesToPostalCodes")
  SubscriptionDto toDto(Subscription subscription);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "country", ignore = true)
  @Mapping(target= "postCodes", ignore = true)
  void updateEntityFromDto(SubscriptionDto dto, @MappingTarget Subscription entity);

  @Named("postCodesToPostalCodes")
  default List<String> postCodesToPostalCodes(Set<PostCode> postCodes) {
    return postCodes.stream().map(PostCode::getPostalCode).toList();
  }
}
