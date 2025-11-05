package nexxus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"nexxus", "nexxus.shared", "nexxus.permission"})
@EnableAspectJAutoProxy
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class NexxusApplication {
  public static void main(String[] args) {
    SpringApplication.run(NexxusApplication.class, args);
  }
}
