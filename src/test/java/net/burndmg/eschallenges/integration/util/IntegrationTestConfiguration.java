package net.burndmg.eschallenges.integration.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

@TestConfiguration
@ComponentScan("net.burndmg.eschallenges")
public class IntegrationTestConfiguration {

    @Bean
    WebTestClient webTestClient(WebApplicationContext applicationContext) {
        return MockMvcWebTestClient.bindToApplicationContext(applicationContext).build();
    }
}
