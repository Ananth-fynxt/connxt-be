package nexxus.request.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestFeeId implements Serializable {
  private String requestId;
  private String feeId;
  private Integer feeVersion;
}
