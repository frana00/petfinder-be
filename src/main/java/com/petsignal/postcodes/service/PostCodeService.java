package com.petsignal.postcodes.service;

import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.repository.PostCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCodeService {
  public final PostCodeRepository postCodeRepository;

  public PostCode findByPostCodeAndCountry(String postCode, String countryCode) {
    return postCodeRepository.findByPostalCodeAndCountryCode(postCode, countryCode)
        .orElseThrow(() -> new ResourceNotFoundException("Postcode", "id", postCode));
  }

}
