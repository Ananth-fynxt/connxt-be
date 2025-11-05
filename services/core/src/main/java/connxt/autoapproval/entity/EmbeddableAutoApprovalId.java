package connxt.autoapproval.entity;

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
public class EmbeddableAutoApprovalId implements Serializable {

  @AutoApprovalId
  @Column(name = "id")
  private String id;

  @Column(name = "version")
  private Integer version;
}
