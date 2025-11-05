package nexxus.systemuser.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.systemuser.entity.SystemUser;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, String> {

  @NonNull
  Optional<SystemUser> findByEmail(@Param("email") String email);

  boolean existsByEmail(@Param("email") String email);

  @NonNull
  List<SystemUser> findAll();

  void deleteById(@Param("id") @NonNull String id);

  Optional<SystemUser> findByUserId(@Param("userId") String userId);
}
