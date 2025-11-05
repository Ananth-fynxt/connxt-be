package connxt.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import connxt.request.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {}
