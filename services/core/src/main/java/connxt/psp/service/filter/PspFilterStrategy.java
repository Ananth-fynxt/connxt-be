package connxt.psp.service.filter;

public interface PspFilterStrategy {

  PspFilterContext apply(PspFilterContext context);

  int getPriority();

  String getStrategyName();

  default boolean shouldApply(PspFilterContext context) {
    return true;
  }
}
