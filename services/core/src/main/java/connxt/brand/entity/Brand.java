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

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  private String email;
}
