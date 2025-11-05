package nexxus.email.template.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import nexxus.email.dto.EmailTemplateContent;
import nexxus.email.template.EmailTemplateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
public class EmailTemplateServiceImpl implements EmailTemplateService {

  private final List<EmailTemplateService> templateServices;

  @Autowired
  public EmailTemplateServiceImpl(List<EmailTemplateService> templateServices) {
    // Filter out this service to avoid circular dependency
    this.templateServices =
        templateServices.stream()
            .filter(service -> !(service instanceof EmailTemplateServiceImpl))
            .toList();

    log.info(
        "EmailTemplateServiceImpl initialized with {} template services:",
        this.templateServices.size());
    for (EmailTemplateService service : this.templateServices) {
      log.info("  - {}", service.getClass().getSimpleName());
    }
  }

  @Override
  public EmailTemplateContent generateTemplate(
      String templateId, Map<String, Object> templateData) {

    log.debug(
        "Generating template for templateId: {}, available services: {}",
        templateId,
        templateServices.size());

    for (EmailTemplateService templateService : templateServices) {
      try {
        log.debug("Trying template service: {}", templateService.getClass().getSimpleName());
        return templateService.generateTemplate(templateId, templateData);
      } catch (IllegalArgumentException e) {
        log.debug(
            "Template service {} doesn't handle templateId: {}",
            templateService.getClass().getSimpleName(),
            templateId);
        continue;
      }
    }

    log.error("No template service found for templateId: {}", templateId);
    throw new IllegalArgumentException(
        "No template service available for template ID: " + templateId);
  }
}
