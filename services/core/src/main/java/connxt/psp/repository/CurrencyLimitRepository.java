package connxt.psp.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.psp.entity.CurrencyLimit;

@Repository
public interface CurrencyLimitRepository extends JpaRepository<CurrencyLimit, String> {

  @Query(
      value =
          "SELECT * FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId",
      nativeQuery = true)
  List<CurrencyLimit> findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Modifying
  @Query(
      value =
          "INSERT INTO currency_limits (brand_id, environment_id, flow_action_id, psp_id, currency, min_value, max_value) VALUES (:brandId, :environmentId, :flowActionId, :pspId, :currency, :minValue, :maxValue)",
      nativeQuery = true)
  void insertCurrencyLimit(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency,
      @Param("minValue") BigDecimal minValue,
      @Param("maxValue") BigDecimal maxValue);

  @Modifying
  @Query(
      value =
          "DELETE FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId",
      nativeQuery = true)
  void deleteByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Query(value = "SELECT * FROM currency_limits WHERE psp_id = :pspId", nativeQuery = true)
  List<CurrencyLimit> findByPspId(@Param("pspId") String pspId);

  @Query(
      value =
          "SELECT COUNT(*) > 0 FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId AND currency = :currency",
      nativeQuery = true)
  boolean existsByCompositeKeyAndCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency);

  @Query(
      value =
          "SELECT psp_id FROM currency_limits WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND currency = :currency",
      nativeQuery = true)
  List<String> findSupportedPspIdsByCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("currency") String currency);
}
