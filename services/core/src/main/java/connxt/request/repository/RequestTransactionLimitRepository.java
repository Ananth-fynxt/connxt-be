package connxt.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.request.entity.RequestTransactionLimit;
import connxt.request.entity.RequestTransactionLimitId;

@Repository
public interface RequestTransactionLimitRepository
    extends JpaRepository<RequestTransactionLimit, RequestTransactionLimitId> {

  List<RequestTransactionLimit> findByRequestId(@Param("requestId") String requestId);
}
