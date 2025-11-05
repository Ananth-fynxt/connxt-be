package connxt.fee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.fee.entity.EmbeddableFeeComponentId;
import connxt.fee.entity.FeeComponent;

@Repository
public interface FeeComponentRepository
    extends JpaRepository<FeeComponent, EmbeddableFeeComponentId> {

  List<FeeComponent> findByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(
      String feeId, Integer feeVersion);

  void deleteByFeeComponentIdFeeId(String feeId);

  void deleteByFeeComponentIdFeeIdAndFeeComponentIdFeeVersion(String feeId, Integer feeVersion);
}
