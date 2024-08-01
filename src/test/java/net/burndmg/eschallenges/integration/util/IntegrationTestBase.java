package net.burndmg.eschallenges.integration.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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


    protected <T> T getSuccessful(String path, Class<T> type) {
        return get(path)
                .expectStatus().is2xxSuccessful()
                .expectBody(type)
                .returnResult()
                .getResponseBody();
    }

    protected WebTestClient.BodyContentSpec getSuccessful(String path) {
        return get(path)
                .expectStatus().is2xxSuccessful()
                .expectBody();
    }

    protected WebTestClient.ResponseSpec get(String path) {
        return webTestClient
                .get()
                .uri("/api" + path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    protected <T> T postSuccessful(String path, Object body, Class<T> responseClass) {
        return post(path, body)
                .expectStatus().is2xxSuccessful()
                .expectBody(responseClass)
                .returnResult()
                .getResponseBody();
    }

    protected WebTestClient.BodyContentSpec postSuccessful(String path, Object body) {
        return post(path, body)
                .expectStatus().is2xxSuccessful()
                .expectBody();
    }

    protected WebTestClient.ResponseSpec post(String path, Object body) {
        return webTestClient
                .post()
                .uri("/api" + path)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    protected WebTestClient.BodyContentSpec putSuccessful(String path, Object body) {
        return put(path, body)
                .expectStatus().is2xxSuccessful()
                .expectBody();
    }

    protected <T> T putSuccessful(String path, Object body, Class<T> responseClass) {
        return put(path, body)
                .expectStatus().is2xxSuccessful()
                .expectBody(responseClass)
                .returnResult()
                .getResponseBody();
    }

    protected WebTestClient.ResponseSpec put(String path, Object body) {
        return webTestClient
                .put()
                .uri("/api" + path)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }
}
