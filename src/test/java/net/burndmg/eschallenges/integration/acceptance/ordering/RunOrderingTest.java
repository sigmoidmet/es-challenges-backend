package net.burndmg.eschallenges.integration.acceptance.ordering;

import net.burndmg.eschallenges.data.dto.run.RunRequest;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.data.model.ChallengeTest;
import net.burndmg.eschallenges.data.model.RunSearchResponseJson;
import net.burndmg.eschallenges.integration.util.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static net.burndmg.eschallenges.integration.util.TestUtil.withAllResult;

@WithMockUser
public class RunOrderingTest extends IntegrationTestBase {

    @Test
    void run_whenUnorderedWithResultInAnyOrder_shouldBeTrue() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "desc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                               [
                                                                                                   { "name": "Dmitry" },
                                                                                                   { "name": "Maria" }
                                                                                               ]
                                                                                             """))
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "Andrijana" },
                                                                                                { "name": "Milos" },
                                                                                                { "name": "Nikola" },
                                                                                                { "name": "Mina" },
                                                                                                { "name": "Petar" }
                                                                                            ]
                                                                                          """))
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(true);
    }

    @Test
    void run_whenOrderedWithResultInTheSameOrder_shouldBeTrue() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "asc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """))
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "A1" },
                                                                                                { "name": "B2" },
                                                                                                { "name": "C3" },
                                                                                                { "name": "D4" },
                                                                                                { "name": "E5" }
                                                                                            ]
                                                                                          """))
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(true);
    }

    @Test
    void run_whenOrderedWithResultInAnotherOrder_shouldBeFalse() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                 "sort": [ { "name.keyword": "desc" } ]
                                               }
                                               """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "Dmitry" },
                                                                                                { "name": "Maria" }
                                                                                            ]
                                                                                          """))
                                                                  .test(withAllResult("""
                                                                                            [
                                                                                                { "name": "A1" },
                                                                                                { "name": "B2" },
                                                                                                { "name": "C3" },
                                                                                                { "name": "D4" },
                                                                                                { "name": "E5" }
                                                                                            ]
                                                                                          """))

                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(false);
    }

    @Test
    void run_whenAggregations_shouldNotDependOnOrder() {
        RunRequest runRequest = new RunRequest("""
                                               {
                                                   "aggs": {
                                                        "unique_count": {
                                                            "cardinality": {
                                                                "field": "name.keyword"
                                                            }
                                                        },
                                                        "missing": {
                                                            "missing": { "field": "price" }
                                                        }
                                                  },
                                                  "sort": [ { "name.keyword": "asc" } ]
                                               }
                                              """);

        Challenge challenge = testIndexer.indexChallenge(Challenge.builder()
                                                                  .title("Find All!")
                                                                  .test(new ChallengeTest(
                                                                          """
                                                                            [
                                                                                { "name": "Dmitry" },
                                                                                { "name": "Maria" }
                                                                            ]
                                                                          """,
                                                                          new RunSearchResponseJson(
                                                                                  """
                                                                                  [
                                                                                    { "name": "Dmitry" },
                                                                                    { "name": "Maria" }
                                                                                  ]
                                                                                  """,
                                                                                  """
                                                                                  {
                                                                                    "unique_count": {
                                                                                        "value": 2
                                                                                    },
                                                                                    "missing": {
                                                                                        "doc_count": 2
                                                                                    }
                                                                                  }
                                                                                  """
                                                                          )
                                                                  ))
                                                                  .test(new ChallengeTest("""
                                                                                            [
                                                                                                { "name": "A1" },
                                                                                                { "name": "A1" },
                                                                                                { "name": "B2" },
                                                                                                { "name": "C3" },
                                                                                                { "name": "C3" }
                                                                                            ]
                                                                                          """,
                                                                                          new RunSearchResponseJson(
                                                                                                  """
                                                                                                  [
                                                                                                    { "name": "A1" },
                                                                                                    { "name": "A1" },
                                                                                                    { "name": "B2" },
                                                                                                    { "name": "C3" },
                                                                                                    { "name": "C3" }
                                                                                                  ]
                                                                                                  """,
                                                                                                  """
                                                                                                  {
                                                                                                    "unique_count": {
                                                                                                        "value": 3
                                                                                                    },
                                                                                                    "missing": {
                                                                                                        "doc_count": 5
                                                                                                    }
                                                                                                  }
                                                                                                  """
                                                                                          )))
                                                                  .expectsTheSameOrder(true)
                                                                  .build());


        postSuccessful("/challenges/" + challenge.id() + "/acceptances/run", runRequest)
                .jsonPath("$.successful").isEqualTo(true);
    }
}
