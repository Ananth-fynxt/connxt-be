package connxt.email;

import connxt.email.dto.EmailRequest;
import connxt.email.dto.EmailResponse;

public interface EmailService {

  EmailResponse sendTemplatedEmail(EmailRequest request);
}
