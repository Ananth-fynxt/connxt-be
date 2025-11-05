package nexxus.fee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nexxus.fee.entity.FeePsp;
import nexxus.fee.entity.FeePspId;

@Repository
public interface FeePspRepository extends JpaRepository<FeePsp, FeePspId> {

  List<FeePsp> findByFeeIdAndFeeVersion(String feeId, Integer feeVersion);

  List<FeePsp> findByPspId(String pspId);

  void deleteByFeeId(String feeId);

  void deleteByFeeIdAndFeeVersion(String feeId, Integer feeVersion);
}
