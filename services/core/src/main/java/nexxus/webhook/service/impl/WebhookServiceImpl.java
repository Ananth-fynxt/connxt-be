package nexxus.webhook.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import nexxus.shared.constants.ErrorCode;
import nexxus.webhook.dto.WebhookDto;
import nexxus.webhook.entity.Webhook;
import nexxus.webhook.repository.WebhookRepository;
import nexxus.webhook.service.WebhookService;
import nexxus.webhook.service.mappers.WebhookMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

  private final WebhookRepository webhookRepository;
  private final WebhookMapper webhookMapper;

  @Override
  @Transactional
  public WebhookDto create(@Valid WebhookDto webhookDto) {
    verifyWebhookNotExists(webhookDto);

    Webhook webhook = webhookMapper.toWebhook(webhookDto);

    Webhook savedWebhook = webhookRepository.save(webhook);
    return webhookMapper.toWebhookDto(savedWebhook);
  }

  @Override
  public List<WebhookDto> readAll(String brandId, String environmentId) {
    return webhookRepository.findByBrandIdAndEnvironmentId(brandId, environmentId).stream()
        .map(webhookMapper::toWebhookDto)
        .collect(Collectors.toList());
  }

  @Override
  public WebhookDto read(String id) {
    Webhook webhook =
        webhookRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.WEBHOOK_NOT_FOUND.getCode()));
    return webhookMapper.toWebhookDto(webhook);
  }

  @Override
  @Transactional
  public WebhookDto update(String id, @Valid WebhookDto webhookDto) {
    Webhook existingWebhook =
        webhookRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.WEBHOOK_NOT_FOUND.getCode()));

    // Check if the new combination would conflict with existing webhooks
    if (!existingWebhook.getBrandId().equals(webhookDto.getBrandId())
        || !existingWebhook.getEnvironmentId().equals(webhookDto.getEnvironmentId())
        || !existingWebhook.getStatusType().equals(webhookDto.getStatusType())) {
      verifyWebhookNotExists(webhookDto);
    }

    Webhook updatedWebhook = webhookMapper.toWebhook(webhookDto);
    updatedWebhook.setId(id);

    Webhook savedWebhook = webhookRepository.save(updatedWebhook);
    return webhookMapper.toWebhookDto(savedWebhook);
  }

  @Override
  @Transactional
  public void delete(String id) {
    if (webhookRepository.findById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.WEBHOOK_NOT_FOUND.getCode());
    }

    webhookRepository.deleteById(id);
  }

  private void verifyWebhookNotExists(WebhookDto webhookDto) {
    if (webhookRepository.existsByBrandIdAndEnvironmentIdAndStatusType(
        webhookDto.getBrandId(), webhookDto.getEnvironmentId(), webhookDto.getStatusType())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.WEBHOOK_ALREADY_EXISTS.getCode());
    }
  }
}
