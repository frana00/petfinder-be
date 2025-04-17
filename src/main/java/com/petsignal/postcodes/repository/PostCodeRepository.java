package com.petsignal.postcodes.repository;

import com.petsignal.postcodes.entity.PostCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCodeRepository extends JpaRepository<PostCode, Long> {
    Optional<PostCode> findByPostalCodeAndCountryCode(String postalCode, String countryCode);
}
