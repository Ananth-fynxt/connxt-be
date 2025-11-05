package connxt.transaction.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

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

  @Autowired private ObjectMapper objectMapper;

  @Autowired private TransactionFlowConfigurationProperties properties;

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

          Optional<FlowDefinition> flowDefinition =
              flowDefinitionRepository.findByFlowTargetIdAndFlowActionId(
                  flowTargetId, flowActionId);

          if (flowDefinition.isEmpty() || flowDefinition.get().getFlowConfiguration() == null) {
            logger.warn(
                "No flow definition found for target: {} and action: {}",
                flowTargetId,
                flowActionId);
            return Collections.emptyList();
          }

          try {
            JsonNode flowConfig = flowDefinition.get().getFlowConfiguration();
            JsonNode currentStatusNode = flowConfig.get(currentStatus.name());

            if (currentStatusNode == null || !currentStatusNode.isArray()) {
              logger.debug(
                  "No valid next statuses found for status: {} in PSP: {} and transaction type: {}",
                  currentStatus,
                  flowTargetId,
                  flowActionId);
              return Collections.emptyList();
            }

            return objectMapper.readValue(
                currentStatusNode.toString(), new TypeReference<List<TransactionStatus>>() {});
          } catch (Exception e) {
            logger.error(
                "Failed to parse next statuses from JSONB for target: {} and action: {}",
                flowTargetId,
                flowActionId,
                e);
            throw new RuntimeException("Failed to parse next statuses from JSONB", e);
          }
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
}
