package nexxus.request.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.request.entity.RequestRiskRule;
import nexxus.request.entity.RequestRiskRuleId;

@Repository
public interface RequestRiskRuleRepository
    extends JpaRepository<RequestRiskRule, RequestRiskRuleId> {

  List<RequestRiskRule> findByRequestId(@Param("requestId") String requestId);
}
