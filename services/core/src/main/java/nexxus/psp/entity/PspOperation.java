package nexxus.psp.entity;

import java.util.List;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import nexxus.shared.constants.Status;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psp_operations")
@IdClass(PspOperationId.class)
@Audited
public class PspOperation {

  //  @Id
  //  @Column(name = "brand_id")
  //  private String brandId;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @Id
  @Column(name = "flow_action_id")
  private String flowActionId;

  @Id
  @Column(name = "flow_definition_id")
  private String flowDefinitionId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;

  @Column(name = "currencies", columnDefinition = "text[]")
  private List<String> currencies;

  @Column(name = "countries", columnDefinition = "text[]")
  private List<String> countries;
}
