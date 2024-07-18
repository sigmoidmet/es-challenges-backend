package net.burndmg.eschallenges.integration.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@TestConfiguration
@ComponentScan("net.burndmg.eschallenges")
public class IntegrationTestConfiguration {

    @Bean
    WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient.bindToApplicationContext(applicationContext)
                            .apply(springSecurity())
                            .configureClient()
                            .build();
    }
}
