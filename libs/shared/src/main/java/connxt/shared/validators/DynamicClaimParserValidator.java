package connxt.shared.validators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.shared.constants.Scope;

@Component
public class DynamicClaimParserValidator {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Pattern OBJECT_HEADER_PATTERN = Pattern.compile("UserInfo\\.([A-Za-z]+)\\(");

  /** Parse any claim value (JSON or structured text) into a list of objects */
  public List<Map<String, Object>> parseClaim(Object claimValue) {
    if (claimValue == null) return Collections.emptyList();

    String claimText = claimValue.toString().trim();

    // Try JSON first
    if (isJson(claimText)) {
      try {
        Object json = objectMapper.readValue(claimText, Object.class);
        if (json instanceof List<?> list) return normalizeList(list);
        if (json instanceof Map<?, ?> map) return List.of(normalizeMap(map));
      } catch (Exception ignored) {
      }
    }

    // Fallback: structured text
    return parseStructuredRecursive(claimText);
  }

  /** Validate that brandId and environmentId exist in JWT claims */
  public boolean validateBrandEnvironmentInClaims(
      Map<String, Object> claims, String scope, String brandId, String environmentId) {

    try {
      if (Scope.SYSTEM.getValue().equals(scope)) {
        // SYSTEM scope: No brands or accessible_brands list - has full access
        return true;
      } else if (Scope.FI.getValue().equals(scope)) {
        // FI scope: has "brands": [] array of values
        Object brandsObj = claims.get("brands");
        List<Map<String, Object>> brandsList = parseClaim(brandsObj);
        String tokenFiId = (String) claims.get("fi_id");

        if (tokenFiId == null || brandsList == null || brandsList.isEmpty()) {
          return false;
        }

        // For FI scope, validate brand belongs to the user's FI
        return validateBrandBelongsToFi(brandsList, brandId, environmentId, tokenFiId);
      } else if (Scope.BRAND.getValue().equals(scope)) {
        // BRAND scope: has "accessible_brands": [] array of values
        Object accessibleBrandsObj = claims.get("accessible_brands");
        List<Map<String, Object>> brandsList = parseClaim(accessibleBrandsObj);

        if (brandsList == null || brandsList.isEmpty()) {
          return false;
        }

        // Check if the requested brand and environment exist in accessible_brands
        return validateBrandEnvironmentInList(brandsList, brandId, environmentId);
      } else if (Scope.EXTERNAL.getValue().equals(scope)) {
        // EXTERNAL scope: validate against specific brand_id and environment_id in claims
        String tokenBrandId = (String) claims.get("brand_id");
        String tokenEnvironmentId = (String) claims.get("environment_id");

        return brandId.equals(tokenBrandId) && environmentId.equals(tokenEnvironmentId);
      } else {
        return false;
      }

    } catch (Exception e) {
      return false;
    }
  }

  /** Validates that a brand and environment exist in a brands list. */
  private boolean validateBrandEnvironmentInList(
      List<Map<String, Object>> brandsList, String brandId, String environmentId) {

    for (Map<String, Object> brand : brandsList) {
      String claimBrandId = (String) brand.get("id");
      if (brandId.equals(claimBrandId)) {
        // Check if the requested environment exists within this brand
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> environments =
            (List<Map<String, Object>>) brand.get("environments");

        if (environments != null) {
          for (Map<String, Object> environment : environments) {
            String claimEnvironmentId = (String) environment.get("id");
            if (environmentId.equals(claimEnvironmentId)) {
              return true;
            }
          }
        }
        break;
      }
    }

    return false;
  }

  /**
   * Validates that a brand belongs to the specified FI. For FI-level access, if the brand is in the
   * user's brands list and the user has the correct fi_id, then they have access.
   */
  private boolean validateBrandBelongsToFi(
      List<Map<String, Object>> brandsList, String brandId, String environmentId, String fiId) {

    for (Map<String, Object> brand : brandsList) {
      String claimBrandId = (String) brand.get("id");

      if (brandId.equals(claimBrandId)) {
        // Brand found in user's brands list - check environment
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> environments =
            (List<Map<String, Object>>) brand.get("environments");

        if (environments != null) {
          for (Map<String, Object> environment : environments) {
            String claimEnvironmentId = (String) environment.get("id");
            if (environmentId.equals(claimEnvironmentId)) {
              return true;
            }
          }
        }
        // Brand found but environment not found
        return false;
      }
    }

    // Brand not found
    return false;
  }

  /** Extract roleId from brand/environment */
  public String extractRoleId(
      List<Map<String, Object>> brands, String brandId, String environmentId) {
    if (brands == null || brandId == null || environmentId == null) return null;

    for (Map<String, Object> brand : brands) {
      if (brandId.equals(brand.get("id"))) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> environments =
            (List<Map<String, Object>>) brand.get("environments");
        if (environments != null) {
          for (Map<String, Object> env : environments) {
            if (environmentId.equals(env.get("id"))) return (String) env.get("roleId");
          }
        }
      }
    }
    return null;
  }

  /** Extract roleId from JWT claims based on scope */
  public String extractRoleIdFromClaims(
      Map<String, Object> claims, String scope, String brandId, String environmentId) {

    if (Scope.SYSTEM.getValue().equals(scope)) {
      // SYSTEM scope: No brands list - extract role from direct claims
      return (String) claims.get("role_id");
    } else if (Scope.FI.getValue().equals(scope)) {
      // FI scope: extract role from brands list
      Object brandsObj = claims.get("brands");
      List<Map<String, Object>> brands = parseClaim(brandsObj);
      return extractRoleId(brands, brandId, environmentId);
    } else if (Scope.BRAND.getValue().equals(scope)) {
      // BRAND scope: extract role from accessible_brands list
      Object accessibleBrandsObj = claims.get("accessible_brands");
      List<Map<String, Object>> accessibleBrands = parseClaim(accessibleBrandsObj);
      return extractRoleId(accessibleBrands, brandId, environmentId);
    } else if (Scope.EXTERNAL.getValue().equals(scope)) {
      // EXTERNAL scope: extract role from direct claims
      return (String) claims.get("role_id");
    }

    return null;
  }

  // ✅ Improved recursive parser that supports deeply nested UserInfo structures
  private List<Map<String, Object>> parseStructuredRecursive(String text) {
    List<Map<String, Object>> result = new ArrayList<>();
    int index = 0;

    while (index < text.length()) {
      Matcher headerMatcher = OBJECT_HEADER_PATTERN.matcher(text);
      if (!headerMatcher.find(index)) break;

      String typeName = headerMatcher.group(1);
      int startParen = headerMatcher.end() - 1;
      int closeParen = findMatchingParenthesis(text, startParen);
      if (closeParen == -1) break;

      String inner = text.substring(startParen + 1, closeParen).trim();
      Map<String, Object> map = parseKeyValues(inner);
      map.put("_type", typeName); // optional: track structure type
      result.add(map);

      index = closeParen + 1;
    }

    return result;
  }

  /** ✅ Key-value parser that recursively detects nested UserInfo values */
  private Map<String, Object> parseKeyValues(String inner) {
    Map<String, Object> map = new LinkedHashMap<>();

    // Custom lightweight tokenizer instead of regex-only, for better nesting support
    int i = 0;
    while (i < inner.length()) {
      int eq = inner.indexOf('=', i);
      if (eq == -1) break;
      String key = inner.substring(i, eq).trim();

      int comma = findNextCommaOrEnd(inner, eq + 1);
      String value = inner.substring(eq + 1, comma).trim();

      if (value.startsWith("UserInfo.") || value.startsWith("[UserInfo.")) {
        List<Map<String, Object>> nested = parseStructuredRecursive(value);
        map.put(key, nested);
      } else {
        map.put(key, value.equals("null") ? null : value);
      }

      i = comma + 1;
    }

    return map;
  }

  /** ✅ Finds the matching closing parenthesis for nested structures */
  private int findMatchingParenthesis(String text, int openIndex) {
    int depth = 0;
    for (int i = openIndex; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '(') depth++;
      else if (c == ')') {
        depth--;
        if (depth == 0) return i;
      }
    }
    return -1;
  }

  /** Finds the next comma that's not inside a nested UserInfo(...) or [...] */
  private int findNextCommaOrEnd(String text, int start) {
    int parenDepth = 0;
    int bracketDepth = 0;
    for (int i = start; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '(') parenDepth++;
      else if (c == ')') parenDepth--;
      else if (c == '[') bracketDepth++;
      else if (c == ']') bracketDepth--;
      else if (c == ',' && parenDepth == 0 && bracketDepth == 0) return i;
    }
    return text.length();
  }

  /** Parses all claims in a JWT map and auto-detects structured values */
  public Map<String, Object> extractAllClaims(Map<String, Object> claims) {
    Map<String, Object> parsedClaims = new LinkedHashMap<>();
    claims.forEach(
        (key, value) -> {
          List<Map<String, Object>> parsed = parseClaim(value);
          parsedClaims.put(key, parsed.isEmpty() ? value : parsed);
        });
    return parsedClaims;
  }

  /** Helpers */
  private boolean isJson(String text) {
    return (text.startsWith("{") && text.endsWith("}"))
        || (text.startsWith("[") && text.endsWith("]"));
  }

  private List<Map<String, Object>> normalizeList(List<?> list) {
    List<Map<String, Object>> normalized = new ArrayList<>();
    for (Object o : list) {
      if (o instanceof Map<?, ?> map) normalized.add(normalizeMap(map));
      else normalized.add(Map.of("value", o));
    }
    return normalized;
  }

  private Map<String, Object> normalizeMap(Map<?, ?> map) {
    Map<String, Object> normalized = new LinkedHashMap<>();
    map.forEach((k, v) -> normalized.put(String.valueOf(k), v));
    return normalized;
  }

  /** Extracts accessible brand IDs based on scope */
  public List<String> extractAccessibleBrandIds(Map<String, Object> claims, String scope) {
    if (Scope.SYSTEM.getValue().equals(scope) || Scope.FI.getValue().equals(scope)) {
      Object brandsObj = claims.get("brands");
      if (brandsObj != null) {
        List<Map<String, Object>> brandsList = parseClaim(brandsObj);
        return brandsList.stream().map(brand -> (String) brand.get("id")).toList();
      }
    } else if (Scope.BRAND.getValue().equals(scope)) {
      Object accessibleBrandsObj = claims.get("accessible_brands");
      if (accessibleBrandsObj != null) {
        List<Map<String, Object>> accessibleBrandsList = parseClaim(accessibleBrandsObj);
        return accessibleBrandsList.stream().map(brand -> (String) brand.get("id")).toList();
      }
    }
    return List.of();
  }

  /** Extracts FI ID based on scope */
  public String extractFiId(Map<String, Object> claims, String scope) {
    if (Scope.FI.getValue().equals(scope)) {
      return (String) claims.get("fi_id");
    }
    return null;
  }

  /** Extracts customer ID based on scope */
  public String extractCustomerId(Map<String, Object> claims, String scope) {
    if (Scope.EXTERNAL.getValue().equals(scope)) {
      return (String) claims.get("customer_id");
    }
    return null;
  }
}
