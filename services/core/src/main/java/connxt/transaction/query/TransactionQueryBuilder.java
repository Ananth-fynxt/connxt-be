package connxt.transaction.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import connxt.transaction.dto.TimeRange;
import connxt.transaction.dto.TransactionSearchCriteria;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.repository.specifications.TransactionSpecifications;

/**
 * Builder class responsible for creating JPA Specifications and Pageable objects for transaction
 * queries based on search criteria.
 */
@Component
public class TransactionQueryBuilder {

  private static final Map<String, String> FIELD_MAPPING = createFieldMapping();

  private static Map<String, String> createFieldMapping() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("txnTime", "createdAt");
    mapping.put("txnStatus", "status");
    mapping.put("psp", "pspId");
    mapping.put("customer", "customerId");
    mapping.put("flowAction", "flowActionId");
    mapping.put("amount", "amount");
    mapping.put("currency", "currency");
    mapping.put("status", "status");
    mapping.put("customerId", "customerId");
    mapping.put("flowActionId", "flowActionId");
    mapping.put("txnType", "transactionType");
    return Map.copyOf(mapping);
  }

  public Pageable createPageable(TransactionSearchCriteria criteria) {
    int page = criteria.getPage() == null || criteria.getPage() < 0 ? 0 : criteria.getPage();
    int size = criteria.getSize() == null || criteria.getSize() <= 0 ? 20 : criteria.getSize();

    Sort.Direction direction =
        criteria.getSortDirection() == null ? Sort.Direction.DESC : criteria.getSortDirection();
    String sortProperty = getSortProperty(criteria.getSortBy());

    return PageRequest.of(page, size, Sort.by(direction, sortProperty));
  }

  /**
   * Builds a specification for transaction filtering based on available filters in the criteria.
   * Only adds specifications for filters that are actually present.
   */
  public Specification<Transaction> buildSpecification(
      String brandId, String environmentId, TransactionSearchCriteria criteria) {
    Specification<Transaction> spec =
        TransactionSpecifications.fieldEquals("brandId", brandId)
            .and(TransactionSpecifications.fieldEquals("environmentId", environmentId))
            .and(TransactionSpecifications.latestVersion());
    return applyFilters(spec, criteria.getFilters());
  }

  private String getSortProperty(String sortBy) {
    if (sortBy == null) return "createdAt";
    return FIELD_MAPPING.getOrDefault(sortBy, "createdAt");
  }

  private Specification<Transaction> applyFilters(
      Specification<Transaction> spec, Map<String, Object> filters) {
    if (filters == null || filters.isEmpty()) {
      return spec;
    }

    for (Map.Entry<String, Object> entry : filters.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value == null) {
        continue;
      }

      if ("timeRange".equals(key)) {
        TimeRange timeRange = extractTimeRange(value);
        if (timeRange != null) {
          TimeRange.DateRange dateRange = timeRange.toDateRange();
          spec =
              spec.and(
                  TransactionSpecifications.createdBetween(dateRange.start(), dateRange.end()));
        }
      } else if (FIELD_MAPPING.containsKey(key)) {
        Object processedValue = processFilterValue(key, value);
        if (processedValue != null) {
          String dbColumnName = FIELD_MAPPING.get(key);
          // Handle status field which can have multiple values
          if ("status".equals(key) && processedValue instanceof List<?> statusList) {
            spec = spec.and(TransactionSpecifications.fieldIn(dbColumnName, statusList));
          } else {
            spec = spec.and(TransactionSpecifications.fieldEquals(dbColumnName, processedValue));
          }
        }
      }
    }
    return spec;
  }

  private Object processFilterValue(String fieldName, Object value) {
    return switch (fieldName) {
      case "status" -> extractStatus(value);
      case "customerId", "pspId", "flowActionId", "currency" -> extractString(value);
      default -> value;
    };
  }

  private static List<TransactionStatus> extractStatus(Object value) {
    if (value instanceof TransactionStatus status) {
      return List.of(status);
    }
    if (value instanceof String str && !str.isBlank()) {
      // Handle comma-separated status values
      return Arrays.stream(str.split(","))
          .map(String::trim)
          .filter(s -> !s.isBlank())
          .map(TransactionStatus::valueOf)
          .collect(Collectors.toList());
    }
    return null;
  }

  private static String extractString(Object value) {
    if (value instanceof String str && !str.isBlank()) {
      return str;
    }
    return null;
  }

  private static TimeRange extractTimeRange(Object value) {
    if (value instanceof TimeRange timeRange) {
      return timeRange;
    }
    if (value instanceof String str && !str.isBlank()) {
      return TimeRange.valueOf(str);
    }
    return null;
  }
}
