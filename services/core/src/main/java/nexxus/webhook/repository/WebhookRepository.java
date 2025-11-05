package nexxus.webhook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.shared.constants.Status;
import nexxus.shared.constants.WebhookStatusType;
import nexxus.webhook.entity.Webhook;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, String> {

  Optional<Webhook> findByBrandIdAndEnvironmentIdAndStatusType(
      String brandId, String environmentId, WebhookStatusType statusType);

  boolean existsByBrandIdAndEnvironmentIdAndStatusType(
      String brandId, String environmentId, WebhookStatusType statusType);

  List<Webhook> findByBrandIdAndEnvironmentIdAndStatusTypeAndStatus(
      String brandId, String environmentId, WebhookStatusType statusType, Status status);

  @Query(
      "SELECT w FROM Webhook w WHERE w.brandId = :brandId AND w.environmentId = :environmentId ORDER BY w.updatedAt DESC")
  List<Webhook> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);
}
