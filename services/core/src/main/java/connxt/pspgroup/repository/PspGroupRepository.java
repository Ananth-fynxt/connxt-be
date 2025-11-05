package connxt.pspgroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.pspgroup.entity.EmbeddablePspGroupId;
import connxt.pspgroup.entity.PspGroup;

@Repository
public interface PspGroupRepository extends JpaRepository<PspGroup, EmbeddablePspGroupId> {

  Optional<PspGroup> findByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);

  @Query(
      "SELECT pg FROM PspGroup pg WHERE pg.brandId = :brandId AND pg.environmentId = :environmentId AND pg.pspGroupId.version = (SELECT MAX(pg2.pspGroupId.version) FROM PspGroup pg2 WHERE pg2.pspGroupId.id = pg.pspGroupId.id)")
  List<PspGroup> findByBrandIdAndEnvironmentId(
      @Param("brandId") String brandId, @Param("environmentId") String environmentId);

  @Query(
      "SELECT pg FROM PspGroup pg WHERE pg.pspGroupId.id = :id ORDER BY pg.pspGroupId.version DESC")
  List<PspGroup> findByIdOrderByVersionDesc(@Param("id") String id);

  @Query(
      "SELECT pg FROM PspGroup pg WHERE pg.pspGroupId.id = :id ORDER BY pg.pspGroupId.version DESC LIMIT 1")
  Optional<PspGroup> findLatestVersionById(String id);

  Optional<PspGroup> findByPspGroupIdIdAndPspGroupIdVersion(String id, Integer version);

  @Query(
      "SELECT COALESCE(MAX(pg.pspGroupId.version), 0) FROM PspGroup pg WHERE pg.pspGroupId.id = :id")
  Integer findMaxVersionById(@Param("id") String id);

  void deleteByPspGroupIdId(String id);

  boolean existsByBrandIdAndEnvironmentIdAndFlowActionIdAndName(
      String brandId, String environmentId, String flowActionId, String name);
}
