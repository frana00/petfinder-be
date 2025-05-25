package com.petsignal.subscriptions.service;

import com.petsignal.alert.entity.AlertType;
import com.petsignal.countries.entity.Country;
import com.petsignal.countries.service.CountryService;
import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.postcodes.entity.PostCode;
import com.petsignal.postcodes.service.PostCodeService;
import com.petsignal.subscriptions.dto.SubscriptionDto;
import com.petsignal.subscriptions.entity.Subscription;
import com.petsignal.subscriptions.mapper.SubscriptionMapper;
import com.petsignal.subscriptions.repository.SubscriptionRepository;
import com.petsignal.user.entity.User;
import com.petsignal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

  private static final String SUBSCRIPTION = "User";

  private final SubscriptionRepository subscriptionRepository;
  private final SubscriptionMapper subscriptionMapper;

  private final PostCodeService postCodeService;
  private final CountryService countryService;
  private final UserService userService;

  public List<SubscriptionDto> getAllSubscriptions() {
    return subscriptionRepository.findAll().stream()
        .map(subscriptionMapper::toDto)
        .toList();
  }

  public List<Subscription> findByTypeAndPostCode(AlertType alertType, PostCode postCode) {
    return subscriptionRepository.findByAlertTypeAndPostalCodeAndCountryCode(alertType, postCode.getPostalCode(),
        postCode.getCountryCode());
  }

  public SubscriptionDto findById(Long id) {
    return subscriptionRepository.findById(id)
        .map(subscriptionMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException(SUBSCRIPTION, "id", id));
  }


  @Transactional
  public SubscriptionDto createSubscription(SubscriptionDto request) {
    Subscription subscription = subscriptionMapper.toEntity(request);

    User user = userService.findEntityById(Long.valueOf(request.getUserId()));
    subscription.setUser(user);

    Country country = countryService.findCountryByCountryCode(request.getCountryCode());
    subscription.setCountry(country);

    setSubscriptionPostCodes(subscription, request);

    return subscriptionMapper.toDto(subscriptionRepository.save(subscription));
  }

  @Transactional
  public SubscriptionDto updateSubscription(Long id, SubscriptionDto request) {
    Subscription subscription = subscriptionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(SUBSCRIPTION, "id", id));

    Country country = countryService.findCountryByCountryCode(request.getCountryCode());
    subscription.setCountry(country);

    subscriptionMapper.updateEntityFromDto(request, subscription);

    setSubscriptionPostCodes(subscription, request);

    return subscriptionMapper.toDto(subscriptionRepository.save(subscription));
  }

  @Transactional
  public void deleteSubscription(Long id) {
    if (!subscriptionRepository.existsById(id)) {
      throw new ResourceNotFoundException(SUBSCRIPTION, "id", id);
    }
    subscriptionRepository.deleteById(id);
  }

  private void setSubscriptionPostCodes(Subscription subscription, SubscriptionDto request) {

    var postCodes = request.getPostCodes().stream()
        .map(postalCode -> postCodeService.findByPostCodeAndCountry(postalCode, request.getCountryCode()))
        .collect(Collectors.toSet());

    subscription.setPostCodes(postCodes);
  }
}
