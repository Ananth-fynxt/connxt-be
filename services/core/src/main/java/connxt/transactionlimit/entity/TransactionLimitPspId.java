package connxt.transactionlimit.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitPspId implements Serializable {

  private String transactionLimitId;
  private Integer transactionLimitVersion;
  private String pspId;
}
