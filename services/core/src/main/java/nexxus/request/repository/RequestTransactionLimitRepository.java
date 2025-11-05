package nexxus.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.request.entity.RequestTransactionLimit;
import nexxus.request.entity.RequestTransactionLimitId;

@Repository
public interface RequestTransactionLimitRepository
    extends JpaRepository<RequestTransactionLimit, RequestTransactionLimitId> {

  List<RequestTransactionLimit> findByRequestId(@Param("requestId") String requestId);
}
