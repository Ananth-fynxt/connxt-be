package nexxus.autoapproval.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoApprovalPspId implements Serializable {

  private String autoApprovalId;
  private Integer autoApprovalVersion;
  private String pspId;
}
