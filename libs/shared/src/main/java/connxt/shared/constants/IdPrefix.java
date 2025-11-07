package connxt.shared.constants;

/**
 * ID Prefix constants for different entity types Based on the TypeScript IdPrefix implementation
 */
public final class IdPrefix {

  // Brand
  public static final String BRAND = "brn";

  // Users
  public static final String USER = "usr";
  public static final String SYSTEM_USER = "sys";
  public static final String SYSTEM_ROLE = "srl";

  // Environment
  public static final String ENVIRONMENT = "env";
  public static final String ENVIRONMENT_SECRET = "sec";
  public static final String ENVIRONMENT_TOKEN = "";

  // Flow
  public static final String FLOW_TYPE = "ftp";
  public static final String FLOW_ACTION = "fat";
  public static final String FLOW_TARGET = "ftg";
  public static final String FLOW_DEFINITION = "fld";

  // PSP
  public static final String PSP = "psp";
  public static final String MAINTENANCE_WINDOW = "mwn";

  // Transactions
  public static final String TRANSACTION = "ortx";

  // Tokens
  public static final String TOKEN = "tok";

  private IdPrefix() {
    // Utility class, prevent instantiation
  }
}
