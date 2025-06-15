package com.petsignal.alert.repository;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.postcodes.entity.PostCode;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

  @Override
  @NonNull
  @Query("SELECT a FROM Alert a WHERE a.id = :id AND a.deleted = false")
  Optional<Alert> findById(@Param("id") Long id);

  @Query("""
        SELECT a FROM Alert a
        LEFT JOIN a.postCode pc
        WHERE (:type IS NULL OR a.type = :type)
          AND (:status IS NULL OR a.status = :status)
          AND (:countryCode IS NULL OR pc.countryCode = :countryCode)
          AND (:postalCode IS NULL OR pc.postalCode = :postalCode)
          AND (a.deleted = false)
      """)
  Page<Alert> findFilteredAlerts(
      @Param("type") AlertType type,
      @Param("status") AlertStatus status,
      @Param("countryCode") String countryCode,
      @Param("postalCode") String postalCode,
      Pageable pageable
  );

  @Query("""
          SELECT a
          FROM Alert a
          JOIN FETCH a.photos
          WHERE a.status = :status
            AND a.updatedAt BETWEEN :from AND :to
            AND a.deleted = false
      """)
  List<Alert> findWithPhotosByStatusAndUpdatedAtBetween(@Param("status") AlertStatus status,
                                                        @Param("from") LocalDateTime from,
                                                        @Param("to") LocalDateTime to);

  List<Alert> findByTypeAndPostCodeAndDeletedFalse(AlertType type, PostCode postCode);

  // Query for finding alerts within a radius using Haversine formula
  @Query(value = """
      SELECT *, 
      (6371 * acos(
          cos(radians(:lat)) * cos(radians(latitude)) * 
          cos(radians(longitude) - radians(:lng)) + 
          sin(radians(:lat)) * sin(radians(latitude))
      )) AS distance_km
      FROM alerts 
      WHERE latitude IS NOT NULL 
        AND longitude IS NOT NULL
        AND (6371 * acos(
            cos(radians(:lat)) * cos(radians(latitude)) * 
            cos(radians(longitude) - radians(:lng)) + 
            sin(radians(:lat)) * sin(radians(latitude))
        )) <= :radius
        AND (:type IS NULL OR type = :type)
        AND status = 'ACTIVE'
        AND deleted = false
      ORDER BY distance_km
      LIMIT 50
      """, nativeQuery = true)
  List<Alert> findAlertsWithinRadius(
      @Param("lat") Double lat, 
      @Param("lng") Double lng, 
      @Param("radius") Double radius,
      @Param("type") String type
  );
}