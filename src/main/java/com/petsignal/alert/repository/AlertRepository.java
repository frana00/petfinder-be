package com.petsignal.alert.repository;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.entity.AlertStatus;
import com.petsignal.alert.entity.AlertType;
import com.petsignal.postcodes.entity.PostCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

  @Query("""
        SELECT a FROM Alert a
        WHERE (:type IS NULL OR a.type = :type)
          AND (:status IS NULL OR a.status = :status)
          AND (:countryCode IS NULL OR a.postCode.countryCode = :countryCode)
          AND (:postalCode IS NULL OR a.postCode.postalCode = :postalCode)
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
      """)
  List<Alert> findWithPhotosByStatusAndUpdatedAtBetween(@Param("status") AlertStatus status,
                                                        @Param("from") LocalDateTime from,
                                                        @Param("to") LocalDateTime to);

  List<Alert> findByTypeAndPostCode(AlertType type, PostCode postCode);
}