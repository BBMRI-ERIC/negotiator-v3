package eu.bbmri_eric.negotiator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"eu.bbmri_eric.negotiator.*"})
@EnableJpaRepositories(basePackages = {"eu.bbmri_eric.negotiator.*"})
public class NegotiatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(NegotiatorApplication.class, args);
  }
}
