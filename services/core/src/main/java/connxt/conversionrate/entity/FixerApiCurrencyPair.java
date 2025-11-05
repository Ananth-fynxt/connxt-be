package connxt.conversionrate.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fixer_api_currency_pairs")
@Builder
public class FixerApiCurrencyPair {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "source_currency", unique = true, nullable = false)
  private String sourceCurrency;

  @Column(name = "target_currency", nullable = false)
  private List<String> targetCurrency;
}
