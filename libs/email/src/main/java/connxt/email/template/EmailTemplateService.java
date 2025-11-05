package connxt.email.template;

import java.util.Map;

import connxt.email.dto.EmailTemplateContent;

public interface EmailTemplateService {

  EmailTemplateContent generateTemplate(String templateId, Map<String, Object> templateData);
}
