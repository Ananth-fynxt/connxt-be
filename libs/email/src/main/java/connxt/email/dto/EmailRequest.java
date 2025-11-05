package connxt.email.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

  private String emailId;
  private String correlationId;
  private List<String> recipients;
  private String templateId;
  private Map<String, Object> templateData;
  private String senderAddress;
  private String description;
}
