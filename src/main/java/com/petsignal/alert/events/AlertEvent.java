package com.petsignal.alert.events;

import com.petsignal.alert.entity.Alert;

public record AlertEvent(Alert alert, Type type) {
  public enum Type {
    CREATED, UPDATED, DELETED, RESOLVED
  }
}
