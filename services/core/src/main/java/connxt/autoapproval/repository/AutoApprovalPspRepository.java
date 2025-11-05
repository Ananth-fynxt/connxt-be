package connxt.autoapproval.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.autoapproval.entity.AutoApprovalPsp;
import connxt.autoapproval.entity.AutoApprovalPspId;

@Repository
public interface AutoApprovalPspRepository
    extends JpaRepository<AutoApprovalPsp, AutoApprovalPspId> {

  List<AutoApprovalPsp> findByAutoApprovalIdAndAutoApprovalVersion(
      String autoApprovalId, Integer autoApprovalVersion);

  List<AutoApprovalPsp> findByPspId(String pspId);

  void deleteByAutoApprovalId(String autoApprovalId);

  void deleteByAutoApprovalIdAndAutoApprovalVersion(
      String autoApprovalId, Integer autoApprovalVersion);
}
