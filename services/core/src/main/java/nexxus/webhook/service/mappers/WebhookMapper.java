package nexxus.webhook.service.mappers;

import org.mapstruct.Mapper;

import nexxus.shared.db.mappers.MapperCoreConfig;
import nexxus.webhook.dto.WebhookDto;
import nexxus.webhook.entity.Webhook;

@Mapper(config = MapperCoreConfig.class)
public interface WebhookMapper {

  WebhookDto toWebhookDto(Webhook webhook);

  Webhook toWebhook(WebhookDto webhookDto);
}
