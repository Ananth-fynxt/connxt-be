package connxt.session.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import connxt.session.entity.WidgetSession;

@Repository
public interface WidgetSessionRepository extends JpaRepository<WidgetSession, String> {

  Optional<WidgetSession> findByIdAndBrandIdAndEnvironmentId(
      String id, String brandId, String environmentId);

  Optional<WidgetSession> findBySessionTokenHash(String sessionTokenHash);

  @Query(
      "SELECT ws FROM WidgetSession ws WHERE ws.brandId = :brandId AND ws.environmentId = :environmentId AND ws.revoked = false AND ws.expiresAt > :now")
  List<WidgetSession> findActiveSessionsByBrandAndEnvironment(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("now") Instant now);

  @Query(
      "SELECT ws FROM WidgetSession ws WHERE ws.customerId = :customerId AND ws.brandId = :brandId AND ws.environmentId = :environmentId AND ws.revoked = false AND ws.expiresAt > :now")
  List<WidgetSession> findActiveSessionsByCustomerAndBrandAndEnvironment(
      @Param("customerId") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("now") Instant now);

  @Modifying
  @Transactional
  @Query(
      "UPDATE WidgetSession ws SET ws.revoked = true WHERE ws.customerId = :customerId AND ws.brandId = :brandId AND ws.environmentId = :environmentId")
  void revokeSessionsByCustomerAndBrandAndEnvironment(
      @Param("customerId") String customerId,
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId);
}
