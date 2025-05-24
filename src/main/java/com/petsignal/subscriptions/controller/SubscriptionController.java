package com.petsignal.subscriptions.controller;

import com.petsignal.subscriptions.dto.SubscriptionDto;
import com.petsignal.subscriptions.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
  
  private final SubscriptionService subscriptionService;

  @GetMapping
  public List<SubscriptionDto> getSubscriptions() {
    return subscriptionService.getAllSubscriptions();
  }

  @GetMapping("/{id}")
  public SubscriptionDto getSubscription(@PathVariable Long id) {
    return subscriptionService.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public SubscriptionDto createSubscription(@Valid @RequestBody SubscriptionDto request) {
    return subscriptionService.createSubscription(request);
  }

  @PutMapping("/{id}")
  public SubscriptionDto updateSubscription(@PathVariable Long id, @Valid @RequestBody SubscriptionDto request) {
    return subscriptionService.updateSubscription(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteSubscription(@PathVariable Long id) {
    subscriptionService.deleteSubscription(id);
  }
}
