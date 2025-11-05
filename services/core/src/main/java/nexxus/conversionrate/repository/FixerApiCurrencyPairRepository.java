package nexxus.conversionrate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import nexxus.conversionrate.entity.FixerApiCurrencyPair;

@Repository
public interface FixerApiCurrencyPairRepository
    extends JpaRepository<FixerApiCurrencyPair, String> {

  @NonNull
  List<FixerApiCurrencyPair> findAll();
}
