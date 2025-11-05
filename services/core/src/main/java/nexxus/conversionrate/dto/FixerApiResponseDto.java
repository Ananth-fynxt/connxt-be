package nexxus.conversionrate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FixerApiResponseDto {

  @JsonProperty("success")
  private boolean success;

  @JsonProperty("timestamp")
  private Long timestamp;

  @JsonProperty("historical")
  private boolean historical;

  @JsonProperty("base")
  private String base;

  @JsonProperty("date")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @JsonProperty("rates")
  private Map<String, BigDecimal> rates;
}
