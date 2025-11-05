package connxt.fee.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee_psps")
@IdClass(FeePspId.class)
public class FeePsp {

  @Id
  @Column(name = "fee_id")
  private String feeId;

  @Id
  @Column(name = "fee_version")
  private Integer feeVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;
}
