package com.petsignal.alert.mapper;

import com.petsignal.alert.dto.AlertRequest;
import com.petsignal.alert.dto.AlertResponse;
import com.petsignal.alert.entity.Alert;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.user.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

  public Alert toEntity(AlertRequest request) {
    if (request == null) {
      return null;
    }

    Alert alert = new Alert();

    BeanUtils.copyProperties(request, alert, "id", "createdAt", "updatedAt", "userId", "postalCode", "countryCode");

    // Handle User relationship
    if (request.getUserId() != null) {
      User user = new User();
      user.setId(request.getUserId());
      alert.setUser(user);
    }

    // Handle PostalCode relationship
    if (request.getPostalCode() != null || request.getCountryCode() != null) {
      PostCode postCode = new PostCode();
      postCode.setPostalCode(request.getPostalCode());
      postCode.setCountryCode(request.getCountryCode());
      alert.setPostCode(postCode);
    }

    return alert;
  }

  public AlertResponse toResponse(Alert alert) {
    if (alert == null) {
      return null;
    }

    AlertResponse response = new AlertResponse();

    BeanUtils.copyProperties(alert, response, "user", "postalCode");

    // Handle User ID
    if (alert.getUser() != null) {
      response.setUserId(alert.getUser().getId());
    }

    // Handle PostalCode and CountryCode
    if (alert.getPostCode() != null) {
      response.setPostalCode(alert.getPostCode().getPostalCode());
      response.setCountryCode(alert.getPostCode().getCountryCode());
    }

    return response;
  }

} 