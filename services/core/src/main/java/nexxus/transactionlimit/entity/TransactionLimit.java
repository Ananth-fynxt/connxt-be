package nexxus.transactionlimit.entity;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.Status;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_limits")
@Builder
public class TransactionLimit extends AuditingEntity {

  @EmbeddedId private EmbeddableTransactionLimitId transactionLimitId;

  @Column(name = "name")
  private String name;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "currency")
  private String currency;

  @Column(name = "countries", columnDefinition = "TEXT[]")
  private String[] countries;

  @Column(name = "customer_tags", columnDefinition = "TEXT[]")
  private String[] customerTags;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;
}
