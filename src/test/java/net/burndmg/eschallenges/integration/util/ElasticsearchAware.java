package net.burndmg.eschallenges.integration.util;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public interface ElasticsearchAware {

    ConditionalElasticsearchContainer conditionalElasticsearchContainer = new ConditionalElasticsearchContainer();

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.cluster-nodes", conditionalElasticsearchContainer::getHost);
    }
}
