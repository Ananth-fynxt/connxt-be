package connxt.email.template.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import connxt.email.dto.EmailTemplateContent;
import connxt.email.template.EmailTemplateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WelcomeEmailTemplate implements EmailTemplateService {

  @Override
  public EmailTemplateContent generateTemplate(
      String templateId, Map<String, Object> templateData) {

    log.info("WelcomeEmailTemplate called with templateId: {}", templateId);

    if (!"welcome-email".equals(templateId)) {
      log.debug("WelcomeEmailTemplate rejecting templateId: {}", templateId);
      throw new IllegalArgumentException("Only 'welcome-email' template ID is supported");
    }

    log.info("WelcomeEmailTemplate generating welcome email template");

    String userEmail = (String) templateData.getOrDefault("userEmail", "");
    String password = (String) templateData.getOrDefault("password", "");
    String loginUrl = (String) templateData.getOrDefault("loginUrl", "http://localhost:5173/login");
    String supportEmail =
        (String) templateData.getOrDefault("supportEmail", "support@connxt.fynxt.io");
    String companyName = (String) templateData.getOrDefault("companyName", "Connxt Platform");

    String subject = "Welcome to " + companyName + " - Your Account is Ready!";
    String htmlContent = buildHtmlContent(userEmail, password, loginUrl, supportEmail, companyName);
    String plainTextContent =
        buildPlainTextContent(userEmail, password, loginUrl, supportEmail, companyName);

    return EmailTemplateContent.builder()
        .subject(subject)
        .htmlContent(htmlContent)
        .plainTextContent(plainTextContent)
        .build();
  }

  private String buildHtmlContent(
      String userEmail, String password, String loginUrl, String supportEmail, String companyName) {
    return String.format(
        """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Welcome to %s</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 0; }
                        .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .credentials { background: #e8f4f8; border-left: 4px solid #2196F3; padding: 20px; margin: 20px 0; border-radius: 5px; }
                        .credentials h3 { margin-top: 0; color: #1976D2; }
                        .credential-item { margin: 10px 0; }
                        .credential-label { font-weight: bold; color: #555; }
                        .credential-value { font-family: monospace; background: #fff; padding: 5px 10px; border-radius: 3px; border: 1px solid #ddd; }
                        .button { display: inline-block; background: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; font-weight: bold; }
                        .button:hover { background: #45a049; }
                        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; font-size: 12px; color: #666; }
                        .security-note { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .security-note strong { color: #856404; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Welcome to %s!</h1>
                        <p>Your account has been successfully created</p>
                    </div>

                    <div class="content">
                        <h2>Hello!</h2>
                        <p>Welcome to %s! We're excited to have you on board. Your account has been created and is ready to use.</p>

                        <div class="credentials">
                            <h3>🔐 Your Login Credentials</h3>
                            <div class="credential-item">
                                <span class="credential-label">Email:</span>
                                <div class="credential-value">%s</div>
                            </div>
                            <div class="credential-item">
                                <span class="credential-label">Password:</span>
                                <div class="credential-value">%s</div>
                            </div>
                        </div>

                        <div class="footer">
                            <p>If you have any questions or need assistance, please don't hesitate to contact our support team at <a href="mailto:%s">%s</a>.</p>
                            <p>Thank you for choosing %s!</p>
                            <p><em>This email was sent automatically. Please do not reply to this email.</em></p>
                        </div>
                    </div>
                </body>
                </html>
                """,
        companyName,
        companyName,
        companyName,
        userEmail,
        password,
        loginUrl,
        supportEmail,
        supportEmail,
        companyName);
  }

  private String buildPlainTextContent(
      String userEmail, String password, String loginUrl, String supportEmail, String companyName) {
    return String.format(
        """
                Welcome to %s!

                Your account has been successfully created and is ready to use.

                LOGIN CREDENTIALS:
                Email: %s
                Password: %s

                If you have any questions or need assistance, please contact our support team at %s.

                Thank you for choosing %s!

                This email was sent automatically. Please do not reply to this email.
                """,
        companyName, userEmail, password, loginUrl, supportEmail, companyName);
  }
}
