package nexxus.auth.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nexxus.auth.entity.Token;
import nexxus.shared.constants.TokenStatus;
import nexxus.shared.constants.TokenType;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

  @Query("SELECT t FROM Token t WHERE t.id = :id AND t.status = :status")
  Optional<Token> findActiveById(@Param("id") String id, @Param("status") TokenStatus status);

  List<Token> findByCustomerId(String customerId);

  @Query("SELECT t FROM Token t WHERE t.customerId = :customerId AND t.status = :status")
  List<Token> findActiveByCustomerId(
      @Param("customerId") String customerId, @Param("status") TokenStatus status);

  Optional<Token> findByTokenHash(String tokenHash);

  @Query("SELECT t FROM Token t WHERE t.expiresAt < :now AND t.status = :status")
  List<Token> findExpiredTokens(
      @Param("now") OffsetDateTime now, @Param("status") TokenStatus status);

  @Query("SELECT COUNT(t) FROM Token t WHERE t.customerId = :customerId AND t.status = :status")
  Long countActiveByCustomerId(
      @Param("customerId") String customerId, @Param("status") TokenStatus status);

  @Query(
      "SELECT t FROM Token t WHERE t.customerId = :customerId AND t.tokenType = :tokenType AND t.status = :status")
  List<Token> findActiveByCustomerIdAndTokenType(
      @Param("customerId") String customerId,
      @Param("tokenType") TokenType tokenType,
      @Param("status") TokenStatus status);

  @Query(
      "SELECT COUNT(t) FROM Token t WHERE t.tokenHash = :tokenHash AND t.status = :status AND t.tokenType = :tokenType")
  Long countActiveToken(
      @Param("tokenHash") String tokenHash,
      @Param("status") TokenStatus status,
      @Param("tokenType") TokenType tokenType);
}
