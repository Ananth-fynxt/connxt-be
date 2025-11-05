package nexxus.webhook.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.shared.constants.WebhookExecutionStatus;
import nexxus.webhook.entity.WebhookLog;

@Repository
public interface WebhookLogRepository extends JpaRepository<WebhookLog, String> {

  Page<WebhookLog> findByWebhookIdOrderByCreatedAtDesc(String webhookId, Pageable pageable);

  List<WebhookLog> findByWebhookIdAndExecutionStatusOrderByCreatedAtDesc(
      String webhookId, WebhookExecutionStatus executionStatus);

  List<WebhookLog> findByWebhookIdAndAttemptNumberOrderByCreatedAtDesc(
      String webhookId, Integer attemptNumber);

  Page<WebhookLog> findByExecutionStatusOrderByCreatedAtDesc(
      WebhookExecutionStatus executionStatus, Pageable pageable);

  List<WebhookLog> findByCorrelationIdOrderByCreatedAtDesc(String correlationId);

  Optional<WebhookLog> findByJobId(String jobId);

  List<WebhookLog> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);

  @Query(
      "SELECT wl FROM WebhookLog wl WHERE wl.webhookId = :webhookId "
          + "AND wl.createdAt BETWEEN :startDate AND :endDate "
          + "ORDER BY wl.createdAt DESC")
  List<WebhookLog> findByWebhookIdAndDateRange(
      @Param("webhookId") String webhookId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);

  long countByWebhookIdAndExecutionStatus(String webhookId, WebhookExecutionStatus executionStatus);

  @Query(
      "SELECT wl FROM WebhookLog wl WHERE wl.executionStatus = :executionStatus "
          + "AND wl.attemptNumber < 3 "
          + "AND (wl.retryAfter IS NULL OR wl.retryAfter <= :currentTime) "
          + "ORDER BY wl.createdAt ASC")
  List<WebhookLog> findFailedLogsReadyForRetry(
      @Param("executionStatus") WebhookExecutionStatus executionStatus,
      @Param("currentTime") LocalDateTime currentTime);

  @Query(
      "SELECT wl FROM WebhookLog wl JOIN Webhook w ON wl.webhookId = w.id WHERE w.brandId = :brandId "
          + "ORDER BY wl.createdAt DESC")
  Page<WebhookLog> findByBrandId(@Param("brandId") String brandId, Pageable pageable);

  @Query(
      "SELECT wl FROM WebhookLog wl JOIN Webhook w ON wl.webhookId = w.id WHERE w.brandId = :brandId "
          + "AND w.environmentId = :environmentId ORDER BY wl.createdAt DESC")
  Page<WebhookLog> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      Pageable pageable);

  @Query(
      "SELECT wl FROM WebhookLog wl JOIN Webhook w ON wl.webhookId = w.id WHERE w.statusType = :statusType "
          + "ORDER BY wl.createdAt DESC")
  Page<WebhookLog> findByStatusType(
      @Param("statusType") nexxus.shared.constants.WebhookStatusType statusType, Pageable pageable);

  @Query(
      "SELECT "
          + "COUNT(wl) as totalAttempts, "
          + "SUM(CASE WHEN wl.isSuccess = true THEN 1 ELSE 0 END) as successfulAttempts "
          + "FROM WebhookLog wl WHERE wl.webhookId = :webhookId")
  Object[] getSuccessRateStats(@Param("webhookId") String webhookId);

  @Query(
      "SELECT wl FROM WebhookLog wl WHERE wl.webhookId = :webhookId "
          + "AND wl.executionTimeMs IS NOT NULL "
          + "ORDER BY wl.executionTimeMs DESC")
  List<WebhookLog> findWithExecutionTime(@Param("webhookId") String webhookId);
}
