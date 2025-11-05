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
public class EmbeddableFeeId implements Serializable {

  @FeeId
  @Column(name = "id")
  private String id;

  @Column(name = "version")
  private Integer version;
}
