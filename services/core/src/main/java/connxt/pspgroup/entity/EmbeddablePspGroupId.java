package connxt.pspgroup.entity;

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
public class EmbeddablePspGroupId implements Serializable {

  @PspGroupId
  @Column(name = "id")
  private String id;

  @Column(name = "version")
  private Integer version;
}
