package connxt.fee.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EmbeddableFeeComponentId implements Serializable {

  @Column(name = "id")
  @FeeComponentId
  private String id;

  @Column(name = "fee_id")
  private String feeId;

  @Column(name = "fee_version")
  private Integer feeVersion;
}
