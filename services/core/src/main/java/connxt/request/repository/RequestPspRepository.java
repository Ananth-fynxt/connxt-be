package connxt.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import connxt.request.entity.RequestPsp;
import connxt.request.entity.RequestPspId;

@Repository
public interface RequestPspRepository extends JpaRepository<RequestPsp, RequestPspId> {

  List<RequestPsp> findByRequestId(@Param("requestId") String requestId);
}
