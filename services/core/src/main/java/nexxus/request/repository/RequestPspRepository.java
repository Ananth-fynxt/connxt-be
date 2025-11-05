package nexxus.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.request.entity.RequestPsp;
import nexxus.request.entity.RequestPspId;

@Repository
public interface RequestPspRepository extends JpaRepository<RequestPsp, RequestPspId> {

  List<RequestPsp> findByRequestId(@Param("requestId") String requestId);
}
