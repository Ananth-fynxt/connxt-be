package connxt.psp.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PspOperationId implements Serializable {
  //  public String brandId;
  public String pspId;
  public String flowActionId;
  public String flowDefinitionId;
}
