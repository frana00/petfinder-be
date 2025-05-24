package com.petsignal.countries.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "countries")
@Data
public class Country {

  @Id
  @Column(name = "country_code")
  private String countryCode;

  @Column
  private String name;
}
