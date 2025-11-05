package nexxus.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO for manual transaction approval requests */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionApprovalRequest {

  @JsonProperty("txnId")
  private String txnId;

  @JsonProperty("decision")
  private ApprovalDecision decision;
}
