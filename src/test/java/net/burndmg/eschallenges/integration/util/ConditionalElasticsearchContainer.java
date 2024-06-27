package net.burndmg.eschallenges.integration.util;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

// Use '-DuseLocalElasticsearch=true' to force using locally running ES instead of a testcontainers instance
@Slf4j
public class ConditionalElasticsearchContainer extends ElasticsearchContainer {

    public ConditionalElasticsearchContainer() {
        super("elasticsearch:8.14.1");
        start();
    }

    @Override
    public void start() {
        if (shouldUseLocallyRunningElasticsearch()) {
            log.info("Integration tests are expecting Elasticsearch to run on localhost:9200");
        } else {
            super.start();
        }
    }

    private boolean shouldUseLocallyRunningElasticsearch() {
        return Boolean.parseBoolean(System.getProperty("useLocalElasticsearch"));
    }
}
