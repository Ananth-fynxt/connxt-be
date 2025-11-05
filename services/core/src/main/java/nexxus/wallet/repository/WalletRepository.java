package nexxus.wallet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nexxus.wallet.entity.Wallet;

import jakarta.persistence.LockModeType;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Wallet a WHERE a.id = :walletId")
  Optional<Wallet> findByWalletId(String walletId);

  List<Wallet> findByBrandCustomerId(String brandCustomerId);

  boolean existsByBrandCustomerIdAndCurrency(String brandCustomerId, String currency);

  List<Wallet> findWalletsByBrandIdAndEnvironmentIdAndBrandCustomerId(
      String brandId, String environmentId, String brandCustomerId);
}
