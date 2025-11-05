package connxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"connxt", "connxt.shared"})
@EnableAspectJAutoProxy
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ConnxtApplication {
  public static void main(String[] args) {
    SpringApplication.run(ConnxtApplication.class, args);
  }
}
