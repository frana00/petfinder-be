package com.petsignal.scheduler.service;

import com.petsignal.exception.ResourceNotFoundException;
import com.petsignal.scheduler.entity.ScheduledJob;
import com.petsignal.scheduler.repository.ScheduledJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledJobService {

  private final ScheduledJobRepository scheduledJobRepository;

  public LocalDateTime getLastSuccessfulRun(String name) {
    return scheduledJobRepository.findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Job", "name", name))
        .getLastSuccessfulRun();
  }

  public void updateLastRunDated(String name, LocalDateTime lastRunDate, boolean isSuccessful) {
    ScheduledJob scheduledJob = scheduledJobRepository.findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Job", "name", name));

    scheduledJob.setLastRun(lastRunDate);
    if (isSuccessful) {
      scheduledJob.setLastSuccessfulRun(lastRunDate);
    }

    scheduledJobRepository.save(scheduledJob);
  }

  public void ensureScheduledJobExists(String jobName) {
    if (scheduledJobRepository.findByName(jobName).isEmpty()) {
      ScheduledJob newJob = new ScheduledJob();
      newJob.setName(jobName);
      newJob.setLastRun(LocalDateTime.now());
      scheduledJobRepository.save(newJob);
    }
  }
}
