package com.petsignal.scheduler.job;

import com.petsignal.alert.entity.Alert;
import com.petsignal.alert.service.AlertService;
import com.petsignal.photos.service.PhotoService;
import com.petsignal.scheduler.service.ScheduledJobService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoDeletionJob {
  private static final String PHOTO_DELETION_JOB = "photo-deletion-job";

  private final ScheduledJobService scheduledJobService;
  private final AlertService alertService;
  private final PhotoService photoService;

  @PostConstruct
  public void verifyScheduledJobsExist() {
    scheduledJobService.ensureScheduledJobExists(PHOTO_DELETION_JOB);
  }

  @Scheduled(cron = "${scheduling.delete-photos.cron}")
  void deletePhotosForResolvedAlerts() {

    log.info("Starting photo deletion job");
    LocalDateTime lastSuccessfulRun = scheduledJobService.getLastSuccessfulRun(PHOTO_DELETION_JOB);
    LocalDateTime fromDate = (lastSuccessfulRun != null ? lastSuccessfulRun : LocalDateTime.now()).minusDays(7);
    LocalDateTime toDate = LocalDateTime.now().minusDays(7);

    boolean isJobSuccessful = false;
    try {
      List<Alert> latestResolvedAlerts = alertService.getLatestResolvedAlerts(fromDate, toDate);
      latestResolvedAlerts.forEach(alert -> photoService.deletePhotos(alert.getPhotos()));
      isJobSuccessful = true;
      log.info("{} job completed successfully", PHOTO_DELETION_JOB);
    } catch (Exception e) {
      log.error("An error occurred while deleting photo for resolved alerts", e);
    } finally {
      scheduledJobService.updateLastRunDated(PHOTO_DELETION_JOB, LocalDateTime.now(), isJobSuccessful);
    }

  }
}
