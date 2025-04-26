package com.petsignal.scheduler.repository;

import com.petsignal.scheduler.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {

  Optional<ScheduledJob> findByName(String name);
}
