package connxt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class FlowBootstrapDebugRunner implements ApplicationRunner {

  private final ApplicationContext applicationContext;

  public FlowBootstrapDebugRunner(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void run(ApplicationArguments args) {
    try {
      Class<?> initializerClass =
          Class.forName("connxt.flow.boot.bootstrap.FlowSchemaInitializer", false, Thread.currentThread().getContextClassLoader());
      Object initializerBean = applicationContext.getBean(initializerClass);
      log.info("FlowBootstrapDebugRunner invoking FlowSchemaInitializer.initialize()");
      initializerClass.getMethod("initialize").invoke(initializerBean);
    } catch (ClassNotFoundException ex) {
      log.info("Flow library not on classpath; skipping FlowSchemaInitializer invocation");
    } catch (Exception ex) {
      log.warn("Failed to invoke FlowSchemaInitializer.initialize(): {}", ex.getMessage(), ex);
    }
  }
}
