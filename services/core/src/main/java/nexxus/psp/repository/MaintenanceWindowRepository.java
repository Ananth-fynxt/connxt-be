package nexxus.psp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.psp.entity.MaintenanceWindow;

@Repository
public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, String> {

  List<MaintenanceWindow> findByPspId(String pspId);

  List<MaintenanceWindow> findByPspIdAndFlowActionId(String pspId, String flowActionId);

  @Modifying
  @Query("DELETE FROM MaintenanceWindow mw WHERE mw.pspId = :pspId")
  void deleteByPspId(@Param("pspId") String pspId);
}
