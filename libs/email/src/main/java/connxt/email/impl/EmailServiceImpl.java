package connxt.email.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.PollerFlux;

import connxt.email.EmailService;
import connxt.email.dto.EmailRequest;
import connxt.email.dto.EmailResponse;
import connxt.email.dto.EmailTemplateContent;
import connxt.email.template.EmailTemplateService;
import connxt.shared.config.properties.EmailProperties;
import connxt.shared.constants.EmailExecutionStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  @Autowired(required = false)
  private EmailAsyncClient emailClient;

  private final EmailProperties emailProperties;
  private final EmailTemplateService emailTemplateService;
  private final ExecutorService executorService = Executors.newFixedThreadPool(5);

  public EmailServiceImpl(
      EmailProperties emailProperties, EmailTemplateService emailTemplateService) {
    this.emailProperties = emailProperties;
    this.emailTemplateService = emailTemplateService;
  }

  @Override
  public EmailResponse sendTemplatedEmail(EmailRequest request) {
    log.info(
        "Sending templated email: templateId={}, recipients={}",
        request.getTemplateId(),
        request.getRecipients() != null ? request.getRecipients().size() : 0);

    EmailRequest preparedRequest = prepareEmailRequest(request);

    CompletableFuture.supplyAsync(
        () -> {
          return executeEmailSending(preparedRequest);
        },
        executorService);

    return EmailResponse.builder()
        .emailId(preparedRequest.getEmailId())
        .correlationId(preparedRequest.getCorrelationId())
        .executionStatus(EmailExecutionStatus.PENDING.getValue())
        .description(preparedRequest.getDescription())
        .isSuccess(null)
        .recipientCount(
            preparedRequest.getRecipients() != null ? preparedRequest.getRecipients().size() : 0)
        .senderAddress(preparedRequest.getSenderAddress())
        .sentAt(LocalDateTime.now())
        .build();
  }

  private EmailRequest prepareEmailRequest(EmailRequest request) {
    String emailId = request.getEmailId();
    if (emailId == null || emailId.isEmpty()) {
      emailId = "email-" + UUID.randomUUID().toString();
    }

    String correlationId = request.getCorrelationId();
    if (correlationId == null || correlationId.isEmpty()) {
      correlationId = "corr-" + UUID.randomUUID().toString();
    }

    String senderAddress = request.getSenderAddress();
    if (senderAddress == null || senderAddress.isEmpty()) {
      senderAddress = emailProperties.getSenderAddress();
    }

    return EmailRequest.builder()
        .emailId(emailId)
        .correlationId(correlationId)
        .recipients(request.getRecipients())
        .templateId(request.getTemplateId())
        .templateData(request.getTemplateData())
        .senderAddress(senderAddress)
        .description(request.getDescription())
        .build();
  }

  private EmailResponse executeEmailSending(EmailRequest request) {
    LocalDateTime sentAt = LocalDateTime.now();

    try {
      if (emailClient == null) {
        throw new IllegalStateException("Email client is not configured");
      }

      if (request.getTemplateId() == null || request.getTemplateId().isEmpty()) {
        throw new IllegalArgumentException("Template ID is required for templated emails");
      }

      if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
        throw new IllegalArgumentException("Recipients are required");
      }

      EmailMessage emailMessage = buildTemplatedEmailMessage(request);

      PollerFlux<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(emailMessage);

      EmailSendResult result = poller.blockLast().getValue();

      log.info(
          "Email sent successfully: emailId={}, operationId={}",
          request.getEmailId(),
          result.getId());

      return EmailResponse.builder()
          .emailId(request.getEmailId())
          .correlationId(request.getCorrelationId())
          .executionStatus(EmailExecutionStatus.SENT.getValue())
          .messageId(result.getId())
          .sentAt(sentAt)
          .completedAt(LocalDateTime.now())
          .description(request.getDescription())
          .isSuccess(true)
          .recipientCount(request.getRecipients().size())
          .senderAddress(request.getSenderAddress())
          .build();

    } catch (Exception e) {
      log.error(
          "Email sending failed: emailId={}, error={}", request.getEmailId(), e.getMessage(), e);

      return EmailResponse.builder()
          .emailId(request.getEmailId())
          .correlationId(request.getCorrelationId())
          .executionStatus(EmailExecutionStatus.FAILED.getValue())
          .errorMessage(e.getMessage())
          .sentAt(sentAt)
          .completedAt(LocalDateTime.now())
          .description(request.getDescription())
          .isSuccess(false)
          .recipientCount(request.getRecipients() != null ? request.getRecipients().size() : 0)
          .senderAddress(request.getSenderAddress())
          .build();
    }
  }

  private EmailMessage buildTemplatedEmailMessage(EmailRequest request) {
    EmailMessage emailMessage = new EmailMessage();

    emailMessage.setSenderAddress(request.getSenderAddress());
    emailMessage.setToRecipients(request.getRecipients().toArray(new String[0]));

    EmailTemplateContent templateContent =
        emailTemplateService.generateTemplate(request.getTemplateId(), request.getTemplateData());

    emailMessage.setSubject(templateContent.getSubject());
    emailMessage.setBodyHtml(templateContent.getHtmlContent());
    emailMessage.setBodyPlainText(templateContent.getPlainTextContent());

    return emailMessage;
  }
}
