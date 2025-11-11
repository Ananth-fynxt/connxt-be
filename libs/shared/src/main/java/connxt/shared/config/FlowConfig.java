package connxt.shared.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.SchedulingException;
import org.springframework.util.ClassUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(
    name = "connxt.flow.enabled",
    havingValue = "true",
    matchIfMissing = false)
@ConditionalOnClass(name = FlowConfig.FLOW_MODULE_CONFIGURATION_CLASS)
@Import(FlowConfig.FlowModuleImportSelector.class)
public class FlowConfig {

  static final String FLOW_MODULE_CONFIGURATION_CLASS =
      "connxt.flow.boot.FlowModuleConfiguration";

  @Bean
  @Lazy(false)
  FlowConfigurationReporter flowConfigurationReporter() {
    log.info("Configuring Flow library explicitly with bootstrap enabled");
    return new FlowConfigurationReporter();
  }

  static class FlowModuleImportSelector implements ImportSelector {

    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
      boolean present = ClassUtils.isPresent(FLOW_MODULE_CONFIGURATION_CLASS, getClass().getClassLoader());
      if (!present) {
        throw new SchedulingException("Flow library is enabled but FlowModuleConfiguration is missing");
      }
      log.info("Importing FlowModuleConfiguration from flow library");
      return new String[] {FLOW_MODULE_CONFIGURATION_CLASS};
    }
  }

  static class FlowConfigurationReporter {

    FlowConfigurationReporter() {
      log.info("Flow library configured successfully");
    }
  }
}
