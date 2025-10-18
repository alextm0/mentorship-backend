package com.mentorship;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
    info = @Info(
        title = "Mentorship API",
        version = "v1",
        description = "API for managing mentorship invitations and connections."
    )
)
public class Application {

  public static void main(String[] args) {
    // Load the .env file and set properties for Spring to find
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    for (DotenvEntry entry : dotenv.entries()) {
      System.setProperty(entry.getKey(), entry.getValue());
    }

    SpringApplication.run(Application.class, args);
  }

}
