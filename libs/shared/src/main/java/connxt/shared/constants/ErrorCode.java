package connxt.shared.constants;

/**
 * Centralized error codes for the Connxt application Each error code follows the pattern:
 * MODULE_OPERATION_ERROR
 */
public enum ErrorCode {
  // General/Common Errors (1000-1099)
  GENERIC_ERROR("1000", "An unexpected error occurred"),
  VALIDATION_ERROR("1001", "Validation failed"),
  INVALID_REQUEST("1002", "Invalid request format"),
  RESOURCE_NOT_FOUND("1003", "Requested resource not found"),
  UNAUTHORIZED("1004", "Unauthorized access"),
  FORBIDDEN("1005", "Access forbidden"),
  CONFLICT("1006", "Resource conflict"),
  RATE_LIMIT_EXCEEDED("1007", "Rate limit exceeded"),
  SERVICE_UNAVAILABLE("1008", "Service temporarily unavailable"),
  EXTERNAL_SERVICE_ERROR("1009", "External service error"),
  DUPLICATE_RESOURCE("1010", "Resource already exists"),
  MULTIPLE_RESULTS("1011", "Multiple resources found with the same criteria"),
  MISSING_REQUIRED_PARAMETER("1012", "Missing required parameter"),
  INVALID_REQUEST_BODY("1013", "Invalid request body format"),
  REQUEST_BODY_REQUIRED("1014", "Request body is required but was not provided"),
  VALIDATION_FAILED_MISSING_PARAMETERS("1015", "Validation failed. Missing or invalid parameters"),
  REQUEST_METHOD_NOT_SUPPORTED("1016", "Request method not supported"),
  INVALID_REQUEST_FORMAT("1017", "Invalid request format"),
  INVALID_JSON_FORMAT("1018", "Invalid JSON format in request body"),
  INVALID_JSON_SYNTAX("1019", "Invalid JSON syntax in request body"),
  ACCESS_DENIED("1020", "Access denied"),
  AUTHENTICATION_FAILED("1021", "Authentication failed"),
  INVALID_CREDENTIALS("1022", "Invalid credentials"),
  UNEXPECTED_ERROR("1023", "An unexpected error occurred. Please try again later."),
  RESPONSE_PARSE_ERROR("1024", "Error parsing API response"),

  // Database Errors (1100-1199)
  DATABASE_ERROR("1100", "Database operation failed"),
  DATABASE_QUERY_ERROR("1101", "Database query error"),
  DATABASE_STRUCTURE_ERROR("1102", "Database structure error"),
  MISSING_DATABASE_ENV_VARS("1103", "Missing required database environment variables"),
  DATABASE_OPERATION_FAILED("1104", "Database operation failed"),
  DATABASE_OPERATION_TIMEOUT("1105", "Database operation timed out"),
  INVALID_DATABASE_OPERATION("1106", "Invalid database operation"),
  DATABASE_FOREIGN_KEY_VIOLATION(
      "1107", "Cannot perform operation: Referenced data does not exist or is still in use"),
  DATABASE_REQUIRED_DATA_MISSING("1108", "Required data is missing"),
  DATABASE_INVALID_DATA("1109", "Invalid data provided"),
  DATABASE_CONNECTION_ISSUE("1110", "Database connection issue"),
  DATABASE_TRANSACTION_CONFLICT("1111", "Database transaction conflict"),
  DATABASE_ACCESS_DENIED("1112", "Database access denied"),
  DATABASE_STORAGE_ISSUE("1113", "Database storage issue"),
  DATABASE_CONSTRAINT_VIOLATION("1114", "Database constraint violation"),
  DATABASE_SYNTAX_ERROR("1115", "Database syntax error"),
  DATABASE_TABLE_NOT_FOUND("1116", "Database table not found"),
  DATABASE_COLUMN_NOT_FOUND("1117", "Database column not found"),
  DATABASE_RELATION_NOT_FOUND("1118", "Database relation not found"),
  DATABASE_PERMISSION_DENIED("1119", "Database permission denied"),
  DATABASE_INSUFFICIENT_PRIVILEGES("1120", "Database insufficient privileges"),
  DATABASE_DISK_FULL("1121", "Database disk full"),
  DATABASE_DISK_SPACE_ISSUE("1122", "Database disk space issue"),

  // Authentication & Authorization Errors (1200-1299)
  AUTH_INVALID_CREDENTIALS("1200", "Invalid credentials"),
  AUTH_TOKEN_EXPIRED("1201", "Authentication token expired"),
  AUTH_TOKEN_INVALID("1202", "Invalid authentication token"),
  AUTH_TOKEN_MISSING("1203", "Authentication token missing"),
  AUTH_INSUFFICIENT_PERMISSIONS("1204", "Insufficient permissions"),
  AUTH_ACCOUNT_LOCKED("1205", "Account is locked"),
  AUTH_ACCOUNT_DISABLED("1206", "Account is disabled"),
  AUTH_PASSWORD_EXPIRED("1207", "Password has expired"),
  AUTH_REFRESH_TOKEN_INVALID("1208", "Invalid refresh token"),
  AUTH_SESSION_EXPIRED("1209", "Session has expired"),

  // Brand Management Errors (1300-1399)
  BRAND_NOT_FOUND("1300", "Brand not found"),
  BRAND_ALREADY_EXISTS("1301", "Brand already exists"),
  BRAND_NAME_INVALID("1302", "Invalid brand name"),
  BRAND_STATUS_INVALID("1303", "Invalid brand status"),
  BRAND_PERMISSION_DENIED("1304", "Insufficient brand permissions"),

  // Environment Management Errors (1400-1499)
  ENVIRONMENT_NOT_FOUND("1400", "Environment not found"),
  ENVIRONMENT_ALREADY_EXISTS("1401", "Environment already exists"),
  ENVIRONMENT_NAME_INVALID("1402", "Invalid environment name"),
  ENVIRONMENT_STATUS_INVALID("1403", "Invalid environment status"),

  // Flow Management Errors (1500-1599)
  FLOW_ACTION_NOT_FOUND("1500", "Flow action not found"),
  FLOW_ACTION_ALREADY_EXISTS("1501", "Flow action already exists"),
  FLOW_ACTION_NAME_INVALID("1502", "Invalid flow action name"),
  FLOW_ACTION_INPUT_SCHEMA_REQUIRED("1503", "Input schema is required"),
  FLOW_ACTION_OUTPUT_SCHEMA_REQUIRED("1504", "Output schema is required"),

  FLOW_TARGET_NOT_FOUND("1510", "Flow target not found"),
  FLOW_TARGET_ALREADY_EXISTS("1511", "Flow target already exists"),
  FLOW_TARGET_NAME_INVALID("1512", "Invalid flow target name"),
  FLOW_TARGET_LOGO_REQUIRED("1513", "Flow target logo is required"),
  FLOW_TARGET_STATUS_REQUIRED("1514", "Flow target status is required"),
  FLOW_TARGET_CREDENTIAL_SCHEMA_REQUIRED("1515", "Credential schema is required"),

  FLOW_TYPE_NOT_FOUND("1520", "Flow type not found"),
  FLOW_TYPE_ALREADY_EXISTS("1521", "Flow type already exists"),
  FLOW_TYPE_NAME_INVALID("1522", "Invalid flow type name"),

  FLOW_DEFINITION_NOT_FOUND("1530", "Flow definition not found"),
  FLOW_DEFINITION_ALREADY_EXISTS("1531", "Flow definition already exists"),
  FLOW_DEFINITION_CODE_REQUIRED("1532", "Code is required"),
  FLOW_DEFINITION_ACTION_ID_REQUIRED("1533", "Flow action ID is required"),
  FLOW_DEFINITION_TARGET_ID_REQUIRED("1534", "Flow target ID is required"),

  // Payment & PSP Errors (1600-1699)
  PSP_NOT_FOUND("1600", "Payment service provider not found"),
  PSP_ALREADY_EXISTS("1601", "Payment service provider already exists"),
  PSP_CONFIGURATION_ERROR("1602", "PSP configuration error"),
  PSP_CONNECTION_ERROR("1603", "PSP connection failed"),
  PSP_TRANSACTION_FAILED("1604", "PSP transaction failed"),
  PSP_INVALID_RESPONSE("1605", "Invalid PSP response"),
  PSP_TIMEOUT("1606", "PSP request timeout"),
  PSP_RATE_LIMIT("1607", "PSP rate limit exceeded"),
  PSP_MAINTENANCE_MODE("1608", "PSP is in maintenance mode"),
  PSP_SERVICE_UNAVAILABLE("1609", "PSP service unavailable"),
  PSP_INVALID_CREDENTIALS("1610", "Invalid PSP credentials"),
  PSP_ACCOUNT_SUSPENDED("1611", "PSP account suspended"),
  PSP_CURRENCY_NOT_SUPPORTED("1612", "Currency not supported by PSP"),
  PSP_TRANSACTION_TYPE_NOT_SUPPORTED("1613", "Transaction type not supported by PSP"),
  PSP_AMOUNT_LIMIT_EXCEEDED("1614", "Amount limit exceeded for PSP"),
  PSP_GEO_RESTRICTION("1615", "PSP geo-restriction applied"),
  PSP_FRAUD_DETECTION("1616", "PSP fraud detection triggered"),
  PSP_SETTLEMENT_FAILED("1617", "PSP settlement failed"),
  PSP_RECONCILIATION_ERROR("1618", "PSP reconciliation error"),
  PSP_WEBHOOK_FAILED("1619", "PSP webhook delivery failed"),
  PSP_STATUS_INVALID("1620", "PSP status is invalid"),
  PSP_OPERATION_NOT_FOUND("1621", "PSP operation not found"),
  PSP_OPERATION_STATUS_INVALID("1622", "PSP operation status is invalid"),

  // Request Processing Errors (1629-1649)
  MISSING_REQUIRED_HEADER("1629", "Required header is missing"),
  MISSING_REQUIRED_HEADERS("1643", "Required headers are missing"),
  INVALID_HEADER_VALUE("1630", "Invalid header value"),
  CORRELATION_ID_MISSING("1631", "Correlation ID is missing"),
  CORRELATION_ID_INVALID("1632", "Invalid correlation ID format"),
  ACCESS_TOKEN_MISSING("1633", "Access token is missing"),
  ACCESS_TOKEN_INVALID("1634", "Invalid access token"),
  ACCESS_TOKEN_EXPIRED("1635", "Access token has expired"),
  BRAND_ID_MISSING("1636", "Brand ID is missing"),
  BRAND_ID_INVALID("1637", "Invalid brand ID format"),
  SECRET_TOKEN_MISSING("1638", "Secret token is missing"),
  SECRET_TOKEN_INVALID("1639", "Invalid secret token format"),
  ENVIRONMENT_TOKEN_MISSING("1640", "Environment token is missing"),
  ENVIRONMENT_TOKEN_INVALID("1641", "Invalid environment token format"),
  NO_VALID_AUTHENTICATION("1642", "No valid authentication method provided"),

  // Permission and Access Control Errors (1650-1699)
  CROSS_BRAND_ACCESS_DENIED(
      "1651", "Cross-brand access denied. You can only access your authorized brands"),
  CROSS_ENVIRONMENT_ACCESS_DENIED(
      "1652", "Cross-environment access denied. You can only access your authorized environment"),
  INSUFFICIENT_PERMISSIONS("1659", "Insufficient permissions to access this resource"),

  // Environment Errors (1700-1799)
  ENVIRONMENT_BRAND_ID_REQUIRED("1712", "Environment brand ID is required"),

  // User Errors (1950-1999)
  USER_NOT_FOUND("2113", "User not found"),
  USER_ALREADY_EXISTS("2114", "User already exists"),
  USER_EMAIL_REQUIRED("2115", "User email is required"),
  USER_EMAIL_INVALID("2116", "User email is invalid"),
  USER_PASSWORD_REQUIRED("2117", "User password is required"),
  USER_PASSWORD_TOO_WEAK("2118", "User password is too weak"),
  USER_NO_ACCESS("2119", "User has no access permissions"),

  PSP_NAME_REQUIRED("1623", "PSP name is required"),
  PSP_CREDENTIAL_REQUIRED("1624", "Credential is required"),
  PSP_BRAND_ID_REQUIRED("1625", "Brand ID is required"),
  PSP_ENVIRONMENT_ID_REQUIRED("1626", "Environment ID is required"),
  PSP_FLOW_TARGET_ID_REQUIRED("1627", "Flow target ID is required"),

  // Transaction Management Errors (1900-1999)
  TRANSACTION_VALIDATION_FAILED("1923", "Transaction validation failed"),
  TRANSACTION_NOT_FOUND("1924", "Transaction not found"),
  TRANSACTION_ALREADY_EXISTS("1925", "Transaction already exists"),
  TRANSACTION_INVALID_STATUS("1926", "Invalid transaction status"),
  TRANSACTION_AMOUNT_INVALID("1927", "Invalid transaction amount"),
  TRANSACTION_CURRENCY_INVALID("1928", "Invalid transaction currency"),
  TRANSACTION_PSP_UNAVAILABLE("1929", "PSP unavailable for transaction"),
  TRANSACTION_TIMEOUT("1930", "Transaction timeout"),
  TRANSACTION_DECLINED("1931", "Transaction declined"),
  TRANSACTION_PROCESSING_ERROR("1932", "Transaction processing error"),
  TRANSACTION_REVERSAL_FAILED("1933", "Transaction reversal failed"),
  TRANSACTION_REFUND_FAILED("1934", "Transaction refund failed"),
  TRANSACTION_SETTLEMENT_FAILED("1935", "Transaction settlement failed"),
  TRANSACTION_DUPLICATE("1936", "Duplicate transaction detected"),
  TRANSACTION_FRAUD_DETECTED("1937", "Fraud detected in transaction"),
  TRANSACTION_LIMIT_EXCEEDED("1938", "Transaction limit exceeded"),
  TRANSACTION_GEO_RESTRICTED("1939", "Transaction geo-restricted"),
  TRANSACTION_NO_VALID_STEPS_FOUND("1940", "No valid transaction steps found"),
  TRANSACTION_MULTIPLE_STEPS_FOUND("1941", "Multiple valid transaction steps found"),
  TRANSACTION_INVALID_TRANSITION_STATUS("1942", "Invalid transaction transition status"),
  TRANSACTION_REQUEST_ID_NOT_FOUND("1943", "Request Id is required"),

  TRANSACTION_AMOUNT_REQUIRED("1976", "Amount is required"),
  TRANSACTION_CURRENCY_REQUIRED("1977", "Currency is required"),
  TRANSACTION_BRAND_ID_REQUIRED("1978", "Brand ID is required"),
  TRANSACTION_ENVIRONMENT_ID_REQUIRED("1979", "Environment ID is required"),
  TRANSACTION_FLOW_ACTION_ID_REQUIRED("1980", "Flow action ID is required"),
  TRANSACTION_USER_ATTRIBUTE_REQUIRED("1981", "User attribute is required"),
  TRANSACTION_LOG_TRANSACTION_ID_REQUIRED("1982", "Transaction ID is required"),
  TRANSACTION_LOG_DATA_REQUIRED("1983", "Log data is required"),

  // User Attribute Errors (2000-2099)
  USER_ATTRIBUTE_ID_REQUIRED("1984", "User ID is required"),
  USER_ATTRIBUTE_FIRST_NAME_REQUIRED("1985", "First name is required"),
  USER_ATTRIBUTE_LAST_NAME_REQUIRED("1986", "Last name is required"),
  USER_ATTRIBUTE_EMAIL_REQUIRED("1987", "Email is required"),
  USER_ATTRIBUTE_PHONE_REQUIRED("1988", "Phone is required"),
  USER_ATTRIBUTE_ADDRESS_REQUIRED("1989", "Address is required"),
  USER_ATTRIBUTE_ADDRESS_LINE1_REQUIRED("1990", "Address line1 is required"),
  USER_ATTRIBUTE_PHONE_CODE_REQUIRED("1991", "Phone code is required"),
  USER_ATTRIBUTE_PHONE_NUMBER_REQUIRED("1992", "Phone number is required"),

  // Permission Validation Errors (1700-1799)
  BRAND_ACCESS_DENIED("1706", "User does not have access to the requested brand"),
  ENVIRONMENT_ACCESS_DENIED("1707", "User does not have access to the requested environment"),
  ROLE_REQUIRED("1708", "User does not have a valid role for this operation"),
  MISSING_CONTEXT("1709", "Required context information is missing"),
  MODULE_NOT_ALLOWED("1710", "Access to the requested module is not allowed"),
  ACTION_NOT_ALLOWED("1711", "The requested action is not allowed"),

  // Simplified Error Messages (2700-2799)
  HEADER_MISSING("2081", "Header missing"),
  AUTHENTICATION_REQUIRED("2082", "Authentication required"),
  INVALID_TOKEN("2083", "Invalid token"),
  INVALID_CORRELATION_ID("2084", "Invalid request"),

  // Token Errors (2800-2899)
  TOKEN_NOT_FOUND("2085", "Token not found"),
  TOKEN_EXPIRED("2086", "Token has expired"),
  TOKEN_REVOKED("2087", "Token has been revoked"),
  TOKEN_INVALID("2088", "Invalid token"),
  TOKEN_MALFORMED("2089", "Malformed token"),
  TOKEN_SIGNATURE_INVALID("2090", "Invalid token signature"),
  TOKEN_BINDING_INVALID("2091", "Invalid token binding"),
  TOKEN_ISSUANCE_FAILED("2093", "Failed to issue token"),
  TOKEN_VALIDATION_FAILED("2094", "Token validation failed"),
  TOKEN_RATE_LIMIT_EXCEEDED("2095", "Token rate limit exceeded"),
  TOKEN_SECRET_GENERATION_FAILED("2097", "Failed to generate token secret"),
  TOKEN_HASH_COMPUTATION_FAILED("2098", "Failed to compute token hash"),
  TOKEN_ENCRYPTION_FAILED("2099", "Failed to encrypt token secret"),
  TOKEN_DECRYPTION_FAILED("2100", "Failed to decrypt token secret"),
  TOKEN_KEY_VERSION_INVALID("2101", "Invalid token key version"),
  TOKEN_ISSUER_INVALID("2102", "Invalid token issuer"),
  TOKEN_AUDIENCE_INVALID("2103", "Invalid token audience"),
  TOKEN_SCOPE_INVALID("2104", "Invalid token scope"),
  TOKEN_JTI_INVALID("2105", "Invalid token JTI"),
  TOKEN_REVOCATION_FAILED("2106", "Failed to revoke token"),
  TOKEN_CLEANUP_FAILED("2107", "Failed to cleanup expired tokens"),
  TOKEN_MISSING("2108", "Token is missing"),

  // Permission Errors (2900-2999)
  PERMISSION_DENIED("2109", "Permission denied"),
  ROLE_NOT_FOUND("2110", "Role not found"),
  PERMISSION_CHECK_FAILED("2111", "Failed to check permissions"),
  INVALID_PERMISSION_CONFIGURATION("2112", "Invalid permission configuration");

  private final String code;
  private final String message;

  ErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return code;
  }
}
