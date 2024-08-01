package net.burndmg.eschallenges.integration.management;

import net.burndmg.eschallenges.data.dto.SaveChallengeDto;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeExample;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_MANAGEMENT_PRIVILEGE;
import static net.burndmg.eschallenges.integration.util.TestUtil.withEmptyResult;

public class UpdateChallengeIntegrationTest extends IntegrationTestBase {

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void updateChallenge() {
        Challenge indexedChallenge = testIndexer.indexRandomChallengeAndReturnIt("test");

        SaveChallengeDto updateChallenge = SaveChallengeDto.builder()
                                                           .title("Some another title")
                                                           .description(indexedChallenge.description())
                                                           .examples(indexedChallenge.examples())
                                                           .jsonTestArrays(List.of(
                                                                   indexedChallenge.tests().get(0).jsonTestArray() + "\n",
                                                                   "[{}]"
                                                           ))
                                                           .jsonIndexMappings(indexedChallenge.jsonIndexMappings())
                                                           .idealRequest(indexedChallenge.idealRequest())
                                                           .build();

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id())

                .jsonPath("$.testsWithResults[0].jsonTestArray").isEqualTo(indexedChallenge.tests().get(0).jsonTestArray())
                .jsonPath("$.testsWithResults[0].jsonExpectedResultArray").isEqualTo("[]")

                .jsonPath("$.testsWithResults[1].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(1))
                .jsonPath("$.testsWithResults[1].jsonExpectedResultArray").isEqualTo("[]");

        getSuccessful("/challenges/" + indexedChallenge.id())
                .jsonPath("$.title").isEqualTo(updateChallenge.title())
                .jsonPath("$.tests[0].jsonTestArray").isEqualTo(indexedChallenge.tests().get(0).jsonTestArray())
                .jsonPath("$.tests[0].jsonExpectedResultArray").isEqualTo(indexedChallenge.tests().get(0).jsonExpectedResultArray())
                .jsonPath("$.tests[1].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(1))
                .jsonPath("$.tests[1].jsonExpectedResultArray").isEqualTo("[]")
                .jsonPath("$.idealRequest").isEqualTo(updateChallenge.idealRequest())
                .jsonPath("$.examples[0].testDataJson").isEqualTo(updateChallenge.examples().get(0).testDataJson())
                .jsonPath("$.examples[0].expectedResult").isEqualTo(updateChallenge.examples().get(0).expectedResult())
                .jsonPath("$.examples[0].explanation").isEqualTo(updateChallenge.examples().get(0).explanation())
                .jsonPath("$.jsonIndexMappings").isEqualTo(updateChallenge.jsonIndexMappings())
                .jsonPath("$.description").isEqualTo(updateChallenge.description());
    }


    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void updateChallenge_whenChangedRequest_shouldReRunTests() {
        Challenge indexedChallenge = testIndexer.indexChallenge(
                testIndexer.randomChallenge("test")
                           .clearTests()
                           .test(withEmptyResult("[ {\"name\": \"cat\"} ]"))
                           .test(withEmptyResult("[ {\"name\": \"cat\"}, {\"name\": \"dog\"} ]"))
                           .build()
        );

        SaveChallengeDto updateChallenge = SaveChallengeDto.builder()
                                                           .title("Find a Cat!")
                                                           .description(indexedChallenge.description())
                                                           .jsonTestArrays(indexedChallenge.tests()
                                                                                           .stream()
                                                                                           .map(ChallengeTest::jsonTestArray)
                                                                                           .toList())
                                                           .examples(indexedChallenge.examples())
                                                           .jsonIndexMappings(indexedChallenge.jsonIndexMappings())
                                                           .idealRequest("""
                                                                         {
                                                                            "query": {
                                                                                "term": {
                                                                                    "name": "cat"
                                                                                }
                                                                            }
                                                                         }
                                                                         """)
                                                           .build();

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id())

                .jsonPath("$.testsWithResults[0].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(0))
                .jsonPath("$.testsWithResults[0].jsonExpectedResultArray").isEqualTo("[{\"name\":\"cat\"}]")

                .jsonPath("$.testsWithResults[1].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(1))
                .jsonPath("$.testsWithResults[1].jsonExpectedResultArray").isEqualTo("[{\"name\":\"cat\"}]");
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void updateChallenge_whenChangedIndexSettings_shouldReRunTests() {
        Challenge indexedChallenge = testIndexer.indexChallenge(
                testIndexer.randomChallenge("test")
                           .title("Find a Dog!")
                           .idealRequest("""
                                         {
                                            "query": {
                                                "term": {
                                                    "name": "dog"
                                                }
                                            }
                                         }
                                         """)
                           .clearTests()
                           .test(withEmptyResult("[ {\"name\": \"cat\"} ]"))
                           .test(new ChallengeTest("[ {\"name\": \"cat\"}, {\"name\": \"dog\"} ]",
                                                   "[{\"name\":\"dog\"}]"))
                           .build()
        );

        SaveChallengeDto updateChallenge = SaveChallengeDto.builder()
                                                           .title(indexedChallenge.title())
                                                           .description(indexedChallenge.description())
                                                           .jsonTestArrays(indexedChallenge.tests()
                                                                                           .stream()
                                                                                           .map(ChallengeTest::jsonTestArray)
                                                                                           .toList())
                                                           .examples(indexedChallenge.examples())
                                                           .jsonIndexMappings("""
                                                                              {
                                                                                "enabled": false
                                                                              }
                                                                              """)
                                                           .idealRequest(indexedChallenge.idealRequest())
                                                           .build();

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id())

                .jsonPath("$.testsWithResults[0].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(0))
                .jsonPath("$.testsWithResults[0].jsonExpectedResultArray").isEqualTo("[]")

                .jsonPath("$.testsWithResults[1].jsonTestArray").isEqualTo(updateChallenge.jsonTestArrays().get(1))
                .jsonPath("$.testsWithResults[1].jsonExpectedResultArray").isEqualTo("[]");
    }

    @Test
    @WithMockUser(authorities = CHALLENGE_MANAGEMENT_PRIVILEGE)
    void updateChallenge_whenAnotherUpdateAlias_shouldUpdateToBoth() {
        Challenge indexedChallenge = testIndexer.indexRandomChallengeAndReturnIt("test");

        SaveChallengeDto updateChallenge = SaveChallengeDto.builder()
                                                           .title("Some another title")
                                                           .description(indexedChallenge.description())
                                                           .jsonTestArrays(indexedChallenge.tests()
                                                                                           .stream()
                                                                                           .map(ChallengeTest::jsonTestArray)
                                                                                           .toList())
                                                           .examples(indexedChallenge.examples())
                                                           .jsonIndexMappings(indexedChallenge.jsonIndexMappings())
                                                           .idealRequest("{}")
                                                           .build();

        testIndexer.switchUpdateAliasTo("another-index");

        putSuccessful("/challenges/" + indexedChallenge.id(), updateChallenge)
                .jsonPath("$.id").isEqualTo(indexedChallenge.id());
    }

    @Test
    @WithMockUser(authorities = "I don't have any")
    void update_whenUserWithoutPrivilege_shouldForbid() {
        SaveChallengeDto challenge = SaveChallengeDto.builder()
                                                     .title("I will create my own challenge, haha!")
                                                     .description("They think, it's secure")
                                                     .jsonTestArray("\"bit\": \"it's not!!\"")
                                                     .example(new ChallengeExample("I am a hacker!",
                                                                                   "haha",
                                                                                   "haha haha"))
                                                     .jsonIndexMappings("\"will\":\"control\"")
                                                     .idealRequest("everything on this service!")
                                                     .build();

        put("/challenges/1", challenge).expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }
}
