package nexxus.flowtype.entity;

import nexxus.shared.db.AuditingEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flow_types")
public class FlowType extends AuditingEntity {
  @Id @FlowTypeId private String id;

  private String name;
}
