package connxt.shared.constants;

/**
 * ID Prefix constants for different entity types Based on the TypeScript IdPrefix implementation
 */
public final class IdPrefix {

  // Brand related
  public static final String BRAND = "brn";

  // User related
  public static final String USER = "usr";

  // Environment related
  public static final String ENVIRONMENT = "env";
  public static final String ENVIRONMENT_SECRET = "sec";

  // Flow related
  public static final String FLOW_TYPE = "ftp";
  public static final String FLOW_ACTION = "fat";
  public static final String FLOW_TARGET = "ftg";
  public static final String FLOW_DEFINITION = "fld";
  public static final String FLOW = "flw";

  // PSP related
  public static final String PSP = "psp";

  // FX related
  public static final String FX_RATE_CONFIG = "fxc";
  public static final String FX_RATE_MARKUP = "fxm";

  // Fee related
  public static final String FEE = "fee";
  public static final String FEE_COMPONENT = "fec";

  // Risk Rule related
  public static final String RISK_RULE = "rrl";
  public static final String RISK_RULE_CUSTOMER_CRITERIA = "rrc";

  // IP Rule related
  public static final String IP_RULE = "ipr";

  // Auth related
  public static final String BRAND_USER = "bru";
  public static final String BRAND_ROLE = "brr";
  public static final String BRAND_PERMISSION = "brp";
  public static final String SYSTEM_USER = "sys";

  // Customer related
  public static final String BRAND_CUSTOMER = "brc";
  public static final String WALLET = "wal";
  public static final String IP_RULE_ENTRY = "ire";

  // Maintenance Window related
  public static final String MAINTENANCE_WINDOW = "mwn";

  // Transaction related
  public static final String TRANSACTION = "ortx";
  public static final String TRANSACTION_LOG = "tnl";

  // Webhook related
  public static final String WEBHOOK = "whk";
  public static final String WEBHOOK_LOG = "whl";

  // Routing Rule related
  public static final String ROUTING_RULE = "rtr";
  public static final String ROUTING_RULE_CONDITION = "rtc";
  public static final String ROUTING_RULE_PSP = "rtp";

  // Permission related
  public static final String PERMISSION = "prm";

  // Transaction Limit related
  public static final String TRANSACTION_LIMIT = "txl";
  public static final String TRANSACTION_LIMIT_PSP_ACTION = "tla";

  // Auto Approval related
  public static final String AUTO_APPROVAL = "apr";

  // PSP Group related
  public static final String PSP_GROUP = "psg";

  // Conversion Rate related
  public static final String CONVERSION_RATE_SETUP = "crs";
  public static final String CONVERSION_RATE = "cvr";

  // Session related
  public static final String SESSION = "ses";

  // Token (no prefix)
  public static final String TOKEN = "tok";
  public static final String ENVIRONMENT_TOKEN = "";

  // Request related
  public static final String REQUEST = "req";
  public static final String REQUEST_PSP = "rqp";

  private IdPrefix() {
    // Utility class, prevent instantiation
  }
}
