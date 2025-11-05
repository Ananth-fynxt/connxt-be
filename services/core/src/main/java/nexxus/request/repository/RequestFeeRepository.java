package nexxus.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.request.entity.RequestFee;
import nexxus.request.entity.RequestFeeId;

@Repository
public interface RequestFeeRepository extends JpaRepository<RequestFee, RequestFeeId> {

  List<RequestFee> findByRequestId(@Param("requestId") String requestId);
}
