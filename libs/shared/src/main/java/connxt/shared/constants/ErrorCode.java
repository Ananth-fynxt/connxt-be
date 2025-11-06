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
  BRAND_NOT_IN_ACCESSIBLE_LIST("1654", "Requested brand is not in your accessible brands list"),
  ENVIRONMENT_NOT_IN_ACCESSIBLE_LIST(
      "1655", "Requested environment is not in your accessible environments list"),
  INVALID_SCOPE_FOR_OPERATION("1656", "Your scope level does not permit this operation"),
  CUSTOMER_ID_MISMATCH("1658", "Customer ID in request does not match your token customer ID"),
  INSUFFICIENT_PERMISSIONS("1659", "Insufficient permissions to access this resource"),

  // Environment Errors (1700-1799)
  ENVIRONMENT_NAME_REQUIRED("1702", "Environment name is required"),
  ENVIRONMENT_SECRET_REQUIRED("1703", "Environment secret is required"),
  ENVIRONMENT_TOKEN_REQUIRED("1704", "Environment token is required"),
  ENVIRONMENT_ORIGIN_REQUIRED("1705", "Environment origin is required"),
  ENVIRONMENT_SUCCESS_REDIRECT_URL_REQUIRED("1706", "Environment success redirect URL is required"),
  ENVIRONMENT_FAILURE_REDIRECT_URL_REQUIRED("1707", "Environment failure redirect URL is required"),
  ENVIRONMENT_BRAND_ID_REQUIRED("1708", "Environment brand ID is required"),
  ENVIRONMENT_CREATED_BY_REQUIRED("1709", "Environment created by is required"),
  ENVIRONMENT_UPDATED_BY_REQUIRED("1710", "Environment updated by is required"),

  // Brand Permission Errors (1800-1899)
  BRAND_PERMISSION_NOT_FOUND("1800", "Brand permission not found"),
  BRAND_PERMISSION_ALREADY_EXISTS("1801", "Brand permission already exists"),
  BRAND_PERMISSION_NAME_REQUIRED("1802", "Brand permission name is required"),
  BRAND_PERMISSION_BRAND_ID_REQUIRED("1803", "Brand permission brand ID is required"),
  BRAND_PERMISSION_PERMISSION_REQUIRED("1804", "Brand permission is required"),

  // Brand Role Errors (1900-1999)
  BRAND_ROLE_NOT_FOUND("1900", "Brand role not found"),
  BRAND_ROLE_ALREADY_EXISTS("1901", "Brand role already exists"),
  BRAND_ROLE_NAME_REQUIRED("1902", "Brand role name is required"),
  BRAND_ROLE_BRAND_ID_REQUIRED("1903", "Brand role brand ID is required"),
  BRAND_ROLE_PERMISSION_ID_REQUIRED("1904", "Brand role permission ID is required"),

  // User Errors (1950-1999)
  USER_NOT_FOUND("2113", "User not found"),
  USER_ALREADY_EXISTS("2114", "User already exists"),
  USER_EMAIL_REQUIRED("2115", "User email is required"),
  USER_EMAIL_INVALID("2116", "User email is invalid"),
  USER_PASSWORD_REQUIRED("2117", "User password is required"),
  USER_PASSWORD_TOO_WEAK("2118", "User password is too weak"),
  USER_NO_ACCESS("2119", "User has no access permissions"),

  // Customer Errors (1960-1979)
  CUSTOMER_NOT_FOUND_BRAND("2120", "Customer not found in brand"),

  // Brand User Errors (2000-2099)
  BRAND_USER_NOT_FOUND("2121", "Brand user not found"),
  BRAND_USER_ALREADY_EXISTS("2122", "Brand user already exists"),
  BRAND_USER_NAME_REQUIRED("2123", "Brand user name is required"),
  BRAND_USER_EMAIL_REQUIRED("2124", "Brand user email is required"),
  BRAND_USER_EMAIL_INVALID("2125", "Brand user email is invalid"),

  // Brand Customer Errors (2100-2199)
  BRAND_CUSTOMER_NOT_FOUND("2126", "Brand customer not found"),
  BRAND_CUSTOMER_ALREADY_EXISTS("2127", "Brand customer already exists"),
  BRAND_CUSTOMER_NAME_REQUIRED("2128", "Brand customer name is required"),
  BRAND_CUSTOMER_EMAIL_REQUIRED("2129", "Brand customer email is required"),
  BRAND_CUSTOMER_EMAIL_INVALID("2130", "Brand customer email is invalid"),
  BRAND_CUSTOMER_COUNTRY_REQUIRED("2131", "Brand customer country is required"),
  BRAND_CUSTOMER_CURRENCY_REQUIRED("2132", "Brand customer currency is required"),
  BRAND_CUSTOMER_INVALID_COMBINATION(
      "2133", "Invalid brand, environment, and customer combination"),

  // Wallet Errors (2200-2299)
  WALLET_NOT_FOUND("2134", "Wallet not found"),
  WALLET_ALREADY_EXISTS("2135", "Wallet already exists"),
  WALLET_CURRENCY_REQUIRED("2136", "Wallet currency is required"),
  WALLET_BALANCE_INVALID("2137", "Wallet balance is invalid"),
  WALLET_INSUFFICIENT_BALANCE("2138", "Insufficient wallet balance"),

  PSP_NAME_REQUIRED("1623", "PSP name is required"),
  PSP_CREDENTIAL_REQUIRED("1624", "Credential is required"),
  PSP_BRAND_ID_REQUIRED("1625", "Brand ID is required"),
  PSP_ENVIRONMENT_ID_REQUIRED("1626", "Environment ID is required"),
  PSP_FLOW_TARGET_ID_REQUIRED("1627", "Flow target ID is required"),

  // Fee Management Errors (1700-1799)
  FEE_NOT_FOUND("1700", "Fee configuration not found"),
  FEE_ALREADY_EXISTS("1701", "Fee configuration already exists"),
  FEE_INVALID_AMOUNT("1709", "Invalid fee amount"),
  FEE_INVALID_PERCENTAGE("1710", "Invalid fee percentage"),
  FEE_CONFIGURATION_ERROR("1711", "Fee configuration error"),
  FEE_NAME_REQUIRED("1712", "Fee name is required"),
  FEE_CURRENCY_REQUIRED("1713", "Currency is required"),
  FEE_CHARGE_FEE_TYPE_REQUIRED("1714", "Charge fee type is required"),
  FEE_BRAND_ID_REQUIRED("1715", "Brand ID is required"),
  FEE_ENVIRONMENT_ID_REQUIRED("1716", "Environment ID is required"),
  FEE_FLOW_ACTION_ID_REQUIRED("1717", "Flow Action ID is required"),
  FEE_COMPONENTS_REQUIRED("1718", "At least one component is required"),
  FEE_COUNTRIES_REQUIRED("1719", "At least one country is required"),
  FEE_PSPS_REQUIRED("1720", "At least one PSP is required"),
  FEE_COMPONENT_TYPE_REQUIRED("1721", "Component type is required"),
  FEE_COMPONENT_AMOUNT_REQUIRED("1722", "Component amount is required"),

  // Conversion Rate Management Errors (1800-1899)
  CONVERSION_RATE_EXPIRED("1805", "Conversion rate has expired"),
  CONVERSION_RATE_INVALID("1806", "Invalid conversion rate"),
  CONVERSION_SOURCE_ERROR("1807", "Conversion rate source error"),
  CONVERSION_MARKUP_ERROR("1808", "Conversion markup configuration error"),
  CONVERSION_CURRENCY_PAIR_INVALID("1809", "Invalid currency pair for conversion"),
  CONVERSION_RATE_CALCULATION_ERROR("1810", "Error calculating conversion rate"),
  CONVERSION_PROVIDER_UNAVAILABLE("1811", "Conversion rate provider unavailable"),
  CONVERSION_LIMIT_EXCEEDED("1812", "Conversion limit exceeded"),
  CONVERSION_TRANSACTION_FAILED("1813", "Conversion transaction failed"),
  CONVERSION_SOURCE_TYPE_REQUIRED("1814", "Source Type is required"),
  CONVERSION_FETCH_OPTION_REQUIRED("1815", "Fetch option is required"),
  CONVERSION_BRAND_ID_REQUIRED("1816", "Brand ID is required"),
  CONVERSION_ENVIRONMENT_ID_REQUIRED("1817", "Environment ID is required"),
  CONVERSION_MARKUP_OPTION_REQUIRED("1818", "Markup Option is required"),
  CONVERSION_SOURCE_CURRENCY_REQUIRED("1819", "Source Currency is required"),
  CONVERSION_TARGET_CURRENCY_REQUIRED("1820", "Target Currency is required"),
  CONVERSION_AMOUNT_REQUIRED("1821", "Amount is required"),

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

  // Transaction Limit Errors (1950-1999)
  TRANSACTION_LIMIT_NOT_FOUND("1944", "Transaction limit configuration not found"),
  TRANSACTION_LIMIT_ALREADY_EXISTS("1945", "Transaction limit configuration already exists"),
  TRANSACTION_LIMIT_NAME_REQUIRED("1946", "Transaction limit name is required"),
  TRANSACTION_LIMIT_BRAND_ID_REQUIRED("1947", "Brand ID is required"),
  TRANSACTION_LIMIT_ENVIRONMENT_ID_REQUIRED("1948", "Environment ID is required"),
  TRANSACTION_LIMIT_CURRENCY_REQUIRED("1949", "Currency is required"),
  TRANSACTION_LIMIT_COUNTRIES_REQUIRED("1950", "At least one country is required"),
  TRANSACTION_LIMIT_CUSTOMER_TAGS_REQUIRED("1951", "At least one customer tag is required"),
  TRANSACTION_LIMIT_PSP_ACTIONS_REQUIRED("1952", "At least one PSP action is required"),
  TRANSACTION_LIMIT_PSPS_REQUIRED("1953", "At least one PSP is required"),
  TRANSACTION_LIMIT_MIN_AMOUNT_REQUIRED("1954", "Minimum amount is required"),
  TRANSACTION_LIMIT_MAX_AMOUNT_REQUIRED("1955", "Maximum amount is required"),
  TRANSACTION_LIMIT_FLOW_ACTION_ID_REQUIRED("1956", "Flow Action ID is required"),
  TRANSACTION_LIMIT_INVALID_AMOUNT_RANGE(
      "1957", "Maximum amount must be greater than minimum amount"),

  // Auto Approval Errors (1980-1999)
  AUTO_APPROVAL_NOT_FOUND("1958", "Auto approval configuration not found"),
  AUTO_APPROVAL_ALREADY_EXISTS("1959", "Auto approval configuration already exists"),
  AUTO_APPROVAL_NAME_REQUIRED("1960", "Auto approval name is required"),
  AUTO_APPROVAL_BRAND_ID_REQUIRED("1961", "Brand ID is required"),
  AUTO_APPROVAL_ENVIRONMENT_ID_REQUIRED("1962", "Environment ID is required"),
  AUTO_APPROVAL_CURRENCY_REQUIRED("1963", "Currency is required"),
  AUTO_APPROVAL_COUNTRIES_REQUIRED("1964", "At least one country is required"),
  AUTO_APPROVAL_FLOW_ACTION_ID_REQUIRED("1965", "Flow Action ID is required"),
  AUTO_APPROVAL_MAX_AMOUNT_REQUIRED("1966", "Maximum amount is required"),
  AUTO_APPROVAL_PSPS_REQUIRED("1967", "At least one PSP is required"),

  // PSP Group Errors (2100-2119)
  PSP_GROUP_NOT_FOUND("1968", "PSP group not found"),
  PSP_GROUP_ALREADY_EXISTS("1969", "PSP group already exists"),
  PSP_GROUP_NAME_REQUIRED("1970", "PSP group name is required"),
  PSP_GROUP_BRAND_ID_REQUIRED("1971", "Brand ID is required"),
  PSP_GROUP_ENVIRONMENT_ID_REQUIRED("1972", "Environment ID is required"),
  PSP_GROUP_FLOW_ACTION_ID_REQUIRED("1973", "Flow Action ID is required"),
  PSP_GROUP_CURRENCY_REQUIRED("1974", "Currency is required"),
  PSP_GROUP_PSPS_REQUIRED("1975", "At least one PSP is required"),
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

  // Risk Management Errors (2100-2199)
  RISK_RULE_NOT_FOUND("1993", "Risk rule not found"),
  RISK_RULE_ALREADY_EXISTS("1994", "Risk rule already exists"),
  RISK_RULE_INVALID("1995", "Invalid risk rule configuration"),
  RISK_THRESHOLD_EXCEEDED("1996", "Risk threshold exceeded"),
  RISK_SCORE_INVALID("1997", "Invalid risk score"),
  RISK_RULE_TYPE_REQUIRED("1998", "Type is required"),
  RISK_RULE_ACTION_REQUIRED("1999", "Action is required"),
  RISK_RULE_CURRENCY_REQUIRED("2000", "Currency is required"),
  RISK_RULE_DURATION_REQUIRED("2001", "Duration is required"),
  RISK_RULE_NAME_REQUIRED("2002", "Name is required"),
  RISK_RULE_MAX_AMOUNT_REQUIRED("2003", "Max amount is required"),
  RISK_RULE_BRAND_ID_REQUIRED("2004", "Brand ID is required"),
  RISK_RULE_ENVIRONMENT_ID_REQUIRED("2005", "Environment ID is required"),
  RISK_RULE_FLOW_ACTION_ID_REQUIRED("2006", "Flow Action ID is required"),
  RISK_RULE_PSPS_REQUIRED("2007", "PSPs list is required"),
  RISK_RULE_PSP_ID_REQUIRED("2008", "PSP ID is required"),
  RISK_RULE_CRITERIA_TYPE_REQUIRED("2009", "criteriaType is required when type is CUSTOMER"),
  RISK_RULE_CRITERIA_VALUE_REQUIRED("2010", "criteriaValue is required when type is CUSTOMER"),

  // Routing Errors (2200-2299)
  ROUTING_RULE_NOT_FOUND("2011", "Routing rule not found"),
  ROUTING_RULE_ALREADY_EXISTS("2012", "Routing rule already exists"),
  ROUTING_RULE_INVALID("2013", "Invalid routing rule configuration"),
  ROUTING_NO_AVAILABLE_PSP("2014", "No available PSP for routing"),
  ROUTING_CONDITION_INVALID("2015", "Invalid routing condition"),
  ROUTING_LAST_RULE_DELETE_FORBIDDEN("2016", "Cannot delete the last routing rule"),
  ROUTING_RULE_BRAND_ID_REQUIRED("2017", "Brand ID is required"),
  ROUTING_RULE_ENVIRONMENT_ID_REQUIRED("2018", "Environment ID is required"),
  ROUTING_RULE_PSP_SELECTION_MODE_REQUIRED("2019", "PSP selection mode is required"),
  ROUTING_RULE_CONDITION_JSON_REQUIRED("2020", "Condition JSON is required"),
  ROUTING_RULE_PSPS_REQUIRED("2021", "At least one PSP is required"),
  ROUTING_RULE_PSP_ID_REQUIRED("2022", "PSP ID is required"),

  // Webhook Errors (2300-2399)
  WEBHOOK_NOT_FOUND("2023", "Webhook not found"),
  WEBHOOK_ALREADY_EXISTS("2024", "Webhook already exists"),
  WEBHOOK_URL_INVALID("2025", "Invalid webhook URL"),
  WEBHOOK_DELIVERY_FAILED("2026", "Webhook delivery failed"),
  WEBHOOK_SIGNATURE_INVALID("2027", "Invalid webhook signature"),
  WEBHOOK_TIMEOUT("2028", "Webhook delivery timeout"),
  WEBHOOK_RETRY_EXCEEDED("2029", "Webhook retry limit exceeded"),
  WEBHOOK_INVALID_PAYLOAD("2030", "Invalid webhook payload"),
  WEBHOOK_ENDPOINT_UNREACHABLE("2031", "Webhook endpoint unreachable"),
  WEBHOOK_SSL_ERROR("2032", "Webhook SSL/TLS error"),
  WEBHOOK_RATE_LIMITED("2033", "Webhook rate limited by endpoint"),
  WEBHOOK_AUTHENTICATION_FAILED("2034", "Webhook authentication failed"),
  WEBHOOK_PROCESSING_ERROR("2035", "Webhook processing error"),
  WEBHOOK_VALIDATION_FAILED("2036", "Webhook validation failed"),
  WEBHOOK_DUPLICATE_EVENT("2037", "Duplicate webhook event received"),
  WEBHOOK_STATUS_TYPE_REQUIRED("2038", "Status type is required"),
  WEBHOOK_URL_REQUIRED("2039", "URL is required"),
  WEBHOOK_BRAND_ID_REQUIRED("2040", "Brand ID is required"),
  WEBHOOK_ENVIRONMENT_ID_REQUIRED("2041", "Environment ID is required"),
  WEBHOOK_RETRY_REQUIRED("2042", "Retry count is required"),
  WEBHOOK_STATUS_REQUIRED("2043", "Status is required"),

  // Conversion Rate Errors (2350-2399)
  CONVERSION_RATE_SETUP_NOT_FOUND("2044", "Conversion rate setup not found"),
  CONVERSION_RATE_SETUP_ALREADY_EXISTS("2045", "Conversion rate setup already exists"),
  CONVERSION_RATE_SETUP_BRAND_ID_REQUIRED("2046", "Brand ID is required"),
  CONVERSION_RATE_SETUP_ENVIRONMENT_ID_REQUIRED("2047", "Environment ID is required"),
  CONVERSION_RATE_SETUP_SOURCE_TYPE_REQUIRED("2048", "Source type is required"),
  CONVERSION_RATE_SETUP_INVALID_CUSTOM_URL("2049", "Invalid custom URL"),
  CONVERSION_RATE_SETUP_INVALID_CUSTOM_INTERVAL("2050", "Invalid custom interval"),

  CONVERSION_RATE_NOT_FOUND("2051", "Conversion rate not found"),
  CONVERSION_RATE_ALREADY_EXISTS("2052", "Conversion rate already exists"),
  CONVERSION_RATE_SOURCE_CURRENCY_REQUIRED("2053", "Source currency is required"),
  CONVERSION_RATE_TARGET_CURRENCY_REQUIRED("2054", "Target currency is required"),
  CONVERSION_RATE_AMOUNT_REQUIRED("2055", "Amount is required"),
  CONVERSION_RATE_BRAND_ID_REQUIRED("2056", "Brand ID is required"),
  CONVERSION_RATE_ENVIRONMENT_ID_REQUIRED("2057", "Environment ID is required"),
  CONVERSION_RATE_INVALID_AMOUNT("2058", "Invalid amount"),
  CONVERSION_RATE_INVALID_VALUE("2059", "Invalid value"),

  // Customer Management Errors (2400-2499)
  CUSTOMER_NOT_FOUND("2060", "Customer not found"),
  CUSTOMER_ALREADY_EXISTS("2061", "Customer already exists"),
  CUSTOMER_DATA_INVALID("2062", "Invalid customer data"),
  CUSTOMER_SYNC_ERROR("2063", "Customer synchronization error"),

  // IP Management Errors (2500-2599)
  IP_ADDRESS_INVALID("2064", "Invalid IP address"),
  IP_RULE_NOT_FOUND("2065", "IP rule not found"),
  IP_RULE_ALREADY_EXISTS("2066", "IP rule already exists"),
  IP_RULE_INVALID("2067", "Invalid IP rule configuration"),
  IP_BLACKLISTED("2068", "IP address is blacklisted"),
  IP_WHITELIST_REQUIRED("2069", "IP address not in whitelist"),

  // Session Management Errors (2600-2699)
  SESSION_NOT_FOUND("2070", "Session not found"),
  SESSION_EXPIRED("2071", "Session has expired"),
  SESSION_REVOKED("2072", "Session has been revoked"),
  SESSION_AUTO_EXTEND_DISABLED("2073", "Session auto-extend is disabled"),
  SESSION_MAX_EXTENSIONS_REACHED("2074", "Maximum session extensions reached"),
  SESSION_INVALID_TOKEN("2075", "Invalid session token"),
  SESSION_CREATION_FAILED("2076", "Failed to create session"),
  SESSION_REFRESH_FAILED("2077", "Failed to refresh session"),
  SESSION_FINGERPRINT_REQUIRED("2078", "Fingerprint is required for session security"),
  SESSION_FINGERPRINT_MISMATCH("2079", "Session fingerprint mismatch - potential security breach"),
  SESSION_CONCURRENCY_LIMIT_EXCEEDED("2080", "Maximum concurrent sessions limit exceeded"),

  // Permission Validation Errors (1700-1799)
  BRAND_ACCESS_DENIED("1702", "User does not have access to the requested brand"),
  ENVIRONMENT_ACCESS_DENIED("1703", "User does not have access to the requested environment"),
  ROLE_REQUIRED("1704", "User does not have a valid role for this operation"),
  MISSING_CONTEXT("1705", "Required context information is missing"),
  MODULE_NOT_ALLOWED("1706", "Access to the requested module is not allowed"),
  ACTION_NOT_ALLOWED("1707", "The requested action is not allowed"),
  EXTERNAL_CUSTOMER_CONTEXT_REQUIRED(
      "1708", "External customer requires brand, environment, and customer context"),
  EXTERNAL_MODULE_ACCESS_DENIED("1709", "External customers cannot access the requested module"),
  EXTERNAL_ACTION_ACCESS_DENIED("1710", "External customers cannot perform the requested action"),
  EXTERNAL_API_BRAND_ACCESS_DENIED(
      "1711", "External API user does not have access to the requested brand"),
  EXTERNAL_API_ENVIRONMENT_ACCESS_DENIED(
      "1712", "External API user does not have access to the requested environment"),

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
  TOKEN_CUSTOMER_MISMATCH("2092", "Token customer mismatch"),
  TOKEN_ISSUANCE_FAILED("2093", "Failed to issue token"),
  TOKEN_VALIDATION_FAILED("2094", "Token validation failed"),
  TOKEN_RATE_LIMIT_EXCEEDED("2095", "Token rate limit exceeded"),
  TOKEN_CUSTOMER_ID_REQUIRED("2096", "Customer ID is required for token"),
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
