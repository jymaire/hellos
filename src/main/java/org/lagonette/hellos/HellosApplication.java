package org.lagonette.hellos;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HellosApplication {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(HellosApplication.class, args);
    }

    @Bean
    public Dotenv loadDotEnv() {
        return Dotenv.load();
    }

}
