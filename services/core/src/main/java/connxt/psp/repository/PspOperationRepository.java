package connxt.psp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.psp.entity.PspOperation;
import connxt.psp.entity.PspOperationId;

@Repository
public interface PspOperationRepository extends JpaRepository<PspOperation, PspOperationId> {

  List<PspOperation> findByPspId(String pspId);

  void deleteByPspId(String pspId);

  Optional<PspOperation> findByPspIdAndFlowActionId(String pspId, String flowActionId);
}
