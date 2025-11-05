package connxt.shared.db;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(
    basePackages = "connxt",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
public class JpaConfig {

  @Bean
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource dataSource() {
    return dataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean(name = "entityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("connxt");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    return em;
  }

  @Primary
  @Bean(name = "transactionManager")
  public JpaTransactionManager transactionManager(
      @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean emf) {
    return new JpaTransactionManager(Objects.requireNonNull(emf.getObject()));
  }
}
