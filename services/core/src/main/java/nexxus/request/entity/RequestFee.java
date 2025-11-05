package nexxus.request.entity;

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
@Table(name = "request_fees")
@IdClass(RequestFeeId.class)
public class RequestFee {

  @Id
  @Column(name = "request_id")
  private String requestId;

  @Id
  @Column(name = "fee_id")
  private String feeId;

  @Id
  @Column(name = "fee_version")
  private Integer feeVersion;
}
