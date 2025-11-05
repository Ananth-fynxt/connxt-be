package connxt.autoapproval.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auto_approval_psps")
@IdClass(AutoApprovalPspId.class)
public class AutoApprovalPsp {

  @Id
  @Column(name = "auto_approval_id")
  private String autoApprovalId;

  @Id
  @Column(name = "auto_approval_version")
  private Integer autoApprovalVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;
}
