package connxt.pspgroup.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PspGroupPspId implements Serializable {

  private String pspGroupId;
  private Integer pspGroupVersion;
  private String pspId;
}
