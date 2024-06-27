package net.burndmg.eschallenges.integration.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public abstract class IntegrationTestBase implements ElasticsearchAware {

    @Autowired
    protected TestIndexer testIndexer;

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    public void init() {
        testIndexer.cleanUpIndex();
    }
}
