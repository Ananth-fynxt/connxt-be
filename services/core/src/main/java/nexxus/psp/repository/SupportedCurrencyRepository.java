package nexxus.psp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.SupportedCurrency;

@Repository
public interface SupportedCurrencyRepository extends JpaRepository<SupportedCurrency, String> {

  @Query(
      value =
          "SELECT * FROM supported_currencies WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId",
      nativeQuery = true)
  List<SupportedCurrency> findByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Modifying
  @Query(
      value =
          "INSERT INTO supported_currencies (brand_id, environment_id, flow_action_id, psp_id, currency) VALUES (:brandId, :environmentId, :flowActionId, :pspId, :currency)",
      nativeQuery = true)
  void insertSupportedCurrency(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId,
      @Param("currency") String currency);

  @Modifying
  @Query(
      value =
          "DELETE FROM supported_currencies WHERE brand_id = :brandId AND environment_id = :environmentId AND flow_action_id = :flowActionId AND psp_id = :pspId",
      nativeQuery = true)
  void deleteByCompositeKey(
      @Param("brandId") String brandId,
      @Param("environmentId") String environmentId,
      @Param("flowActionId") String flowActionId,
      @Param("pspId") String pspId);

  @Query(value = "SELECT * FROM supported_currencies WHERE psp_id = :pspId", nativeQuery = true)
  List<SupportedCurrency> findByPspId(@Param("pspId") String pspId);
}
