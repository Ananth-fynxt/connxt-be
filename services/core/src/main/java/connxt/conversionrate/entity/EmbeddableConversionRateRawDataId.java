package connxt.conversionrate.entity;

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
public class EmbeddableConversionRateRawDataId implements Serializable {

  @ConversionRateRawDataId
  @Column(name = "id")
  private String id;

  @Column(name = "version")
  private Integer version;
}
