package connxt.brandcustomer.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.constants.Scope;
import connxt.shared.constants.UserStatus;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "brand_customer")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandCustomer extends AuditingEntity {

  @Id @BrandCustomerId private String id;

  @Column(name = "brand_id", nullable = false)
  private String brandId;

  @Column(name = "environment_id", nullable = false)
  private String environmentId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "tag")
  private String tag;

  @Column(name = "account_type")
  private String accountType;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "currencies", nullable = false)
  private String[] currencies;

  @JdbcTypeCode(SqlTypes.JSON)
  @Type(JsonBinaryType.class)
  @Column(name = "customer_meta", nullable = false, columnDefinition = "jsonb")
  private JsonNode customerMeta;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.UserStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "user_status")
  @Builder.Default
  private UserStatus status = UserStatus.ACTIVE;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.Scope"))
  @Enumerated(EnumType.STRING)
  @Column(name = "scope", nullable = false, columnDefinition = "scope")
  @Builder.Default
  private Scope scope = Scope.EXTERNAL;
}
