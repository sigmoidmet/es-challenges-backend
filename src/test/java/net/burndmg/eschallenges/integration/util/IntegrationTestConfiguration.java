package net.burndmg.eschallenges.integration.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan("net.burndmg.eschallenges")
public class IntegrationTestConfiguration {

//    @Bean
//    WebTestClient webTestClient(WebApplicationContext applicationContext) {
//        return MockMvcWebTestClient.bindToApplicationContext(applicationContext).build();
//    }
}
