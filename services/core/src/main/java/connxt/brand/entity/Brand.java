package connxt.brand.entity;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends AuditingEntity {

  @Id @BrandId private String id;

  @Column(name = "fi_id", nullable = false)
  private String fiId;

  @Column(name = "currencies", nullable = false)
  private String[] currencies;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;
}
