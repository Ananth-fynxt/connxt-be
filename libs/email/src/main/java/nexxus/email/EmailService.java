package nexxus.email;

import nexxus.email.dto.EmailRequest;
import nexxus.email.dto.EmailResponse;

public interface EmailService {

  EmailResponse sendTemplatedEmail(EmailRequest request);
}
