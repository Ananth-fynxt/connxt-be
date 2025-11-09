package connxt.flow.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;

import connxt.flowaction.service.FlowActionService;

@AutoConfiguration
@ComponentScan(basePackages = "connxt.flow")
@ConditionalOnClass(FlowActionService.class)
public class FlowAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  FlowSchemaInitializer flowSchemaInitializer(JdbcTemplate jdbcTemplate) {
    return new FlowSchemaInitializer(jdbcTemplate);
  }
}
