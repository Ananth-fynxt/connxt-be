package nexxus.shared.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Configuration;

import nexxus.shared.db.PostgreSQLEnumType;

@Configuration
public class HibernateConfig implements TypeContributor {

  @SuppressWarnings("deprecation")
  @Override
  public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
    // Register custom PostgreSQL enum type
    typeContributions.contributeType(new PostgreSQLEnumType(), "pgsql_enum");
  }
}
