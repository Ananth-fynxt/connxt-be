package connxt.email.dto;

import java.time.LocalDateTime;

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
public class EmailResponse {

  private String emailId;
  private String correlationId;
  private String executionStatus;
  private String messageId;
  private String errorMessage;
  private LocalDateTime sentAt;
  private LocalDateTime completedAt;
  private String description;
  private Boolean isSuccess;
  private Integer recipientCount;
  private String senderAddress;
}
