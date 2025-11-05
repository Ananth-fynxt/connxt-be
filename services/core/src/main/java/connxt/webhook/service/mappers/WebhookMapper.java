package connxt.webhook.service.mappers;

import org.mapstruct.Mapper;

import connxt.shared.db.mappers.MapperCoreConfig;
import connxt.webhook.dto.WebhookDto;
import connxt.webhook.entity.Webhook;

@Mapper(config = MapperCoreConfig.class)
public interface WebhookMapper {

  WebhookDto toWebhookDto(Webhook webhook);

  Webhook toWebhook(WebhookDto webhookDto);
}
