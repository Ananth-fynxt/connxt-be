package nexxus.email.template;

import java.util.Map;

import nexxus.email.dto.EmailTemplateContent;

public interface EmailTemplateService {

  EmailTemplateContent generateTemplate(String templateId, Map<String, Object> templateData);
}
