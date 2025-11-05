package connxt.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Data transfer object for email template content. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateContent {

  private String subject;
  private String htmlContent;
  private String plainTextContent;
}
