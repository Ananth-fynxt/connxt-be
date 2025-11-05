package nexxus.transaction.repository.specifications;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.regex.Pattern;

import org.springframework.data.jpa.domain.Specification;

import nexxus.transaction.entity.Transaction;

public final class TransactionSpecifications {

  private TransactionSpecifications() {}

  // Pattern to validate field names (only alphanumeric and underscores)
  private static final Pattern VALID_FIELD_NAME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

  /**
   * Generic method for equality filters - replaces all the individual equals methods Field names
   * are validated to prevent potential injection attacks
   */
  public static Specification<Transaction> fieldEquals(String fieldName, Object value) {
    validateFieldName(fieldName);
    if (value == null) {
      throw new IllegalArgumentException("Filter value cannot be null");
    }
    return (root, query, builder) -> builder.equal(root.get(fieldName), value);
  }

  /**
   * Generic method for IN filters - handles multiple values for a field Field names are validated
   * to prevent potential injection attacks
   */
  public static Specification<Transaction> fieldIn(String fieldName, Collection<?> values) {
    validateFieldName(fieldName);
    if (values == null || values.isEmpty()) {
      throw new IllegalArgumentException("Filter values cannot be null or empty");
    }
    return (root, query, builder) -> root.get(fieldName).in(values);
  }

  /**
   * Validates that field name contains only safe characters
   *
   * @param fieldName the field name to validate
   * @throws IllegalArgumentException if field name is invalid
   */
  private static void validateFieldName(String fieldName) {
    if (fieldName == null || fieldName.isBlank()) {
      throw new IllegalArgumentException("Field name cannot be null or blank");
    }
    if (!VALID_FIELD_NAME.matcher(fieldName).matches()) {
      throw new IllegalArgumentException("Invalid field name: " + fieldName);
    }
  }

  /** Date range filter for created timestamp */
  public static Specification<Transaction> createdBetween(LocalDateTime from, LocalDateTime to) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Date range parameters cannot be null");
    }
    return (root, query, builder) -> builder.between(root.get("createdAt"), from, to);
  }

  /** Latest version filter using subquery */
  public static Specification<Transaction> latestVersion() {
    return (root, query, builder) -> {
      if (query != null) {
        query.distinct(true);
      }
      var subquery =
          query != null
              ? query.subquery(Integer.class)
              : builder.createQuery().subquery(Integer.class);
      var subRoot = subquery.from(Transaction.class);
      subquery
          .select(builder.max(subRoot.get("id").get("version")))
          .where(builder.equal(subRoot.get("id").get("txnId"), root.get("id").get("txnId")));
      return builder.equal(root.get("id").get("version"), subquery);
    };
  }
}
