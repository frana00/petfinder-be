package com.petsignal.posts.mapper;

import com.petsignal.alert.entity.Alert;
import com.petsignal.posts.dto.PostDto;
import com.petsignal.posts.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PostMapper {


  @Mapping(target = "alert", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "user", ignore = true)
  Post toEntity(PostDto postDto);

  @Mapping(target = "alertId", source = "alert.id")
  @Mapping(target = "username", source = "user.username")
  PostDto toDto(Post post);


  @Named("alertIdToAlert")
  default Alert alerIdToAlert(Long alertId) {
    if (alertId == null) {
      return null;
    }
    Alert alert = new Alert();
    alert.setId(alertId);
    return alert;
  }
}
