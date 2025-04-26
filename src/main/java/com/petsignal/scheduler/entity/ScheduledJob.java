package com.petsignal.scheduler.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_jobs")
@Data
@NoArgsConstructor
public class ScheduledJob {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  String name;

  @Column(name = "last_run", nullable = false)
  LocalDateTime lastRun;

  @Column(name = "last_successful_run", nullable = false)
  LocalDateTime lastSuccessfulRun;
}
