package connxt.pspgroup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.pspgroup.entity.PspGroupPsp;
import connxt.pspgroup.entity.PspGroupPspId;

@Repository
public interface PspGroupPspRepository extends JpaRepository<PspGroupPsp, PspGroupPspId> {

  List<PspGroupPsp> findByPspGroupIdAndPspGroupVersion(String pspGroupId, Integer pspGroupVersion);

  List<PspGroupPsp> findByPspId(String pspId);

  void deleteByPspGroupId(String pspGroupId);

  void deleteByPspGroupIdAndPspGroupVersion(String pspGroupId, Integer pspGroupVersion);
}
