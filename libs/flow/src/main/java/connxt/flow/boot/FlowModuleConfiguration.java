package connxt.flow.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import connxt.flow.boot.bootstrap.FlowSchemaInitializer;
import connxt.flow.boot.config.FlowComponentConfiguration;
import connxt.flow.boot.scheduler.FlowBootstrapTaskScheduler;
import connxt.flow.boot.scheduler.ImmediateFlowBootstrapTaskScheduler;
import connxt.flowaction.service.FlowActionService;

@Configuration(proxyBeanMethods = false)
@Import(FlowComponentConfiguration.class)
@ConditionalOnClass(FlowActionService.class)
@ConditionalOnProperty(prefix = "connxt.flow", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
public class FlowModuleConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowModuleConfiguration.class);

  @Bean
  FlowSchemaInitializer flowSchemaInitializer(JdbcTemplate jdbcTemplate) {
    LOGGER.info("Creating FlowSchemaInitializer with configured JdbcTemplate");
    return new FlowSchemaInitializer(jdbcTemplate);
  }

  @Bean
  @ConditionalOnMissingBean(FlowBootstrapTaskScheduler.class)
  FlowBootstrapTaskScheduler flowBootstrapTaskScheduler() {
    LOGGER.info("FlowBootstrapTaskScheduler bean missing; using immediate async scheduler");
    return new ImmediateFlowBootstrapTaskScheduler();
  }

  @Bean
  ApplicationRunner flowSchemaBootstrapRunner(
      FlowBootstrapTaskScheduler scheduler, FlowSchemaInitializer initializer) {
    return args -> {
      LOGGER.info("Scheduling flow schema bootstrap task");
      scheduler.schedule(initializer::initialize);
    };
  }
}
