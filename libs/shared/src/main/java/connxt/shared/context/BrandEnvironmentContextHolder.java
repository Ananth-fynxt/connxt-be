package connxt.shared.context;

public class BrandEnvironmentContextHolder {

  private static final ThreadLocal<BrandEnvironmentContext> contextHolder = new ThreadLocal<>();

  private BrandEnvironmentContextHolder() {}

  public static void setContext(BrandEnvironmentContext context) {
    if (context != null) {
      contextHolder.set(context);
    }
  }

  public static BrandEnvironmentContext getContext() {
    return contextHolder.get();
  }

  public static void clear() {
    BrandEnvironmentContext context = contextHolder.get();
    if (context != null) {}
    contextHolder.remove();
  }

  public static String getBrandId() {
    BrandEnvironmentContext context = getContext();
    return context != null ? context.getBrandId() : null;
  }

  public static String getEnvironmentId() {
    BrandEnvironmentContext context = getContext();
    return context != null ? context.getEnvironmentId() : null;
  }

  public static String getUserId() {
    BrandEnvironmentContext context = getContext();
    return context != null ? context.getUserId() : null;
  }

  public static String getScope() {
    BrandEnvironmentContext context = getContext();
    return context != null ? context.getScope() : null;
  }

  public static String getAuthType() {
    BrandEnvironmentContext context = getContext();
    return context != null ? context.getAuthType() : null;
  }
}
