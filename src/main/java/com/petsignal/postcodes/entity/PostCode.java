package com.petsignal.postcodes.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "postal_codes")
public class PostCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country_code", nullable = false)
    private String countryCode;
    
}
