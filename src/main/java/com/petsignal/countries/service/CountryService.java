package com.petsignal.countries.service;

import com.petsignal.countries.entity.Country;
import com.petsignal.countries.repository.CountryRepository;
import com.petsignal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

  private static final String COUNTRY = "Country";

  private final CountryRepository countryRepository;

  public Country findCountryByCountryCode(String countryCode) {
    return countryRepository.findByCountryCode(countryCode)
        .orElseThrow(() -> new ResourceNotFoundException(COUNTRY, "countryCode", countryCode));

  }
}
