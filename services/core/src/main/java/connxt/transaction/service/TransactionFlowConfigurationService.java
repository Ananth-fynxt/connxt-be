package connxt.transaction.service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import connxt.flowaction.entity.FlowAction;
import connxt.flowaction.repository.FlowActionRepository;
import connxt.flowdefinition.entity.FlowDefinition;
import connxt.flowdefinition.repository.FlowDefinitionRepository;
import connxt.transaction.config.TransactionFlowConfigurationProperties;
import connxt.transaction.dto.TransactionStatus;

import jakarta.annotation.PostConstruct;

@Service
public class TransactionFlowConfigurationService {

  private static final Logger logger =
      LoggerFactory.getLogger(TransactionFlowConfigurationService.class);

  @Autowired private FlowDefinitionRepository flowDefinitionRepository;

  @Autowired private TransactionFlowConfigurationProperties properties;

  @Autowired private FlowActionRepository flowActionRepository;

  private Cache<String, List<TransactionStatus>> nextStatusCache;

  @PostConstruct
  public void initializeCache() {
    this.nextStatusCache =
        Caffeine.newBuilder()
            .maximumSize(properties.getMaximumSize())
            .expireAfterWrite(properties.getExpireAfterWrite())
            .recordStats()
            .build();
  }

  private String generateCacheKey(
      String flowTargetId, String flowActionId, TransactionStatus currentStatus) {
    return flowTargetId + "_" + flowActionId + "_" + currentStatus.name();
  }

  public List<TransactionStatus> getNextStatuses(
      String flowTargetId, String flowActionId, TransactionStatus currentStatus) {
    String cacheKey = generateCacheKey(flowTargetId, flowActionId, currentStatus);

    return nextStatusCache.get(
        cacheKey,
        key -> {
          logger.debug("Loading next statuses from database for key: {}", key);

          Optional<FlowDefinition> flowDefinitionOptional =
              flowDefinitionRepository.findByFlowTargetIdAndFlowActionId(
                  flowTargetId, flowActionId);

          if (flowDefinitionOptional.isEmpty()) {
            logger.warn(
                "No flow definition found for target: {} and action: {}",
                flowTargetId,
                flowActionId);
            return Collections.emptyList();
          }

          FlowDefinition flowDefinition = flowDefinitionOptional.get();
          List<TransactionStatus> fromConfiguration =
              deriveNextStatusesFromConfiguration(
                  flowDefinition.getFlowConfiguration(), currentStatus);
          if (!fromConfiguration.isEmpty()) {
            return fromConfiguration;
          }

          return deriveNextStatusesFromAction(flowActionId, currentStatus);
        });
  }

  public boolean isValidTransition(
      String flowTargetId,
      String flowActionId,
      TransactionStatus currentStatus,
      TransactionStatus nextStatus) {
    List<TransactionStatus> allowedTransitions =
        getNextStatuses(flowTargetId, flowActionId, currentStatus);
    return allowedTransitions.contains(nextStatus);
  }

  public Optional<FlowDefinition> getFlowDefinition(String flowTargetId, String flowActionId) {
    return flowDefinitionRepository.findByFlowTargetIdAndFlowActionId(flowTargetId, flowActionId);
  }

  public List<FlowDefinition> getAllFlowDefinitions() {
    logger.debug("Loading all flow definitions from database");
    return flowDefinitionRepository.findAll();
  }

  public void reloadFlowDefinition(String flowTargetId, String flowActionId) {
    nextStatusCache
        .asMap()
        .keySet()
        .removeIf(key -> key.startsWith(flowTargetId + "_" + flowActionId + "_"));
    logger.info(
        "Reloaded flow definition for target: {} and action: {}", flowTargetId, flowActionId);
  }

  public void reloadAllFlowConfigurations() {
    logger.info("Reloading all transaction flow configurations...");
    nextStatusCache.invalidateAll();
  }

  public Map<String, Object> getCacheStats() {
    Map<String, Object> stats = new java.util.HashMap<>();
    stats.put("cacheSize", nextStatusCache.estimatedSize());
    stats.put("hitRate", nextStatusCache.stats().hitRate());
    stats.put("missRate", nextStatusCache.stats().missRate());
    stats.put("hitCount", nextStatusCache.stats().hitCount());
    stats.put("missCount", nextStatusCache.stats().missCount());
    stats.put("evictionCount", nextStatusCache.stats().evictionCount());
    stats.put("cachedKeys", nextStatusCache.asMap().keySet());
    return stats;
  }

  public void invalidateCacheEntry(String flowTargetId, String flowActionId) {
    nextStatusCache
        .asMap()
        .keySet()
        .removeIf(key -> key.startsWith(flowTargetId + "_" + flowActionId + "_"));
    logger.debug(
        "Invalidated cache entries for target: {} and action: {}", flowTargetId, flowActionId);
  }

  public void invalidateAllCache() {
    nextStatusCache.invalidateAll();
    logger.info("Invalidated all cache entries");
  }

  private List<TransactionStatus> deriveNextStatusesFromConfiguration(
      JsonNode flowConfiguration, TransactionStatus currentStatus) {
    if (flowConfiguration == null || flowConfiguration.isNull()) {
      return Collections.emptyList();
    }

    JsonNode currentStatusNode = flowConfiguration.get(currentStatus.name());
    if (currentStatusNode == null || !currentStatusNode.isArray()) {
      return Collections.emptyList();
    }

    List<TransactionStatus> statuses = new java.util.ArrayList<>(currentStatusNode.size());
    currentStatusNode.forEach(
        node -> {
          if (node.isTextual()) {
            toTransactionStatus(node.asText()).ifPresent(statuses::add);
          }
        });
    return statuses;
  }

  private List<TransactionStatus> deriveNextStatusesFromAction(
      String flowActionId, TransactionStatus currentStatus) {
    Optional<FlowAction> flowActionOptional = flowActionRepository.findById(flowActionId);
    if (flowActionOptional.isEmpty()) {
      logger.warn("Flow action {} not found while deriving next statuses", flowActionId);
      return Collections.emptyList();
    }

    FlowAction flowAction = flowActionOptional.get();
    if (flowAction.getSteps() == null || flowAction.getSteps().isEmpty()) {
      return Collections.emptyList();
    }

    List<TransactionStatus> statusSequence =
        flowAction.getSteps().stream()
            .map(this::toTransactionStatus)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

    int currentIndex = statusSequence.indexOf(currentStatus);
    if (currentIndex == -1 || currentIndex == statusSequence.size() - 1) {
      return Collections.emptyList();
    }

    return statusSequence.subList(currentIndex + 1, statusSequence.size());
  }

  private Optional<TransactionStatus> toTransactionStatus(String value) {
    if (!StringUtils.hasText(value)) {
      return Optional.empty();
    }
    try {
      return Optional.of(TransactionStatus.valueOf(value.trim().toUpperCase(Locale.ROOT)));
    } catch (IllegalArgumentException ex) {
      logger.warn("Unsupported transaction status '{}' defined in flow action", value);
      return Optional.empty();
    }
  }
}
