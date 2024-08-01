package net.burndmg.eschallenges.unit.util;

import net.burndmg.eschallenges.infrastructure.util.JsonsDiscriminator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonsDiscriminatorTest {

    @Test
    void whenInitiallyEqualJsons_shouldFillPresentInInitialSetCollection() {
        Set<String> jsons = Set.of("{\"1\": \"1\"}", "{\"2\": \"1\"}", "{\"3\": \"1\"}", "{\"4\": \"1\"}");
        JsonsDiscriminator.of(jsons)
                          .with(jsons, Object::toString)
                          .into((presentInInitialSet, notPresentInDiscriminationSet) -> {
                                 assertEquals(jsons.size(), presentInInitialSet.size());
                                 assertTrue(notPresentInDiscriminationSet.isEmpty());
                                 return true;
                             });
    }

    @Test
    void whenInitiallyEqualJsonsWithDifferentWhitespaces_shouldFillPresentInInitialSetCollection() {
        Set<String> initialJsons = Set.of("{\"1\": \"1\"}", "{\"2\": \"1\"}", "{\"3\": \"1\"}", "{\"4\": \"1\"}");
        JsonsDiscriminator
                .of(initialJsons)
                .with(Set.of("{\"1\":     \n \"1\"}", "{\"2\": \"1\"}", "{\"3\":        \"1\"}", "{\"4\":  \n\"1\"}"),
                      Object::toString)
                .into((presentInInitialSet, notPresentInDiscriminationSet) -> {
                    assertEquals(initialJsons.size(), presentInInitialSet.size());
                    assertTrue(notPresentInDiscriminationSet.isEmpty());
                    return true;
                });
    }

    @Test
    void whenCompletelyNewJsons_shouldFillOnlyNotPresentInDiscriminationSetCollection() {
        Set<String> initialJsons = Set.of("{\"1\": \"1\"}", "{\"2\": \"1\"}", "{\"3\": \"1\"}", "{\"4\": \"1\"}");
        JsonsDiscriminator.of(initialJsons)
                          .with(Set.of("{}"), Object::toString)
                          .into((presentInInitialSet, notPresentInDiscriminationSet) -> {
                                 assertTrue(presentInInitialSet.isEmpty());
                                 assertEquals(initialJsons.size(), notPresentInDiscriminationSet.size());
                                 return true;
                             });
    }

    @Test
    void whenBothNewAndOldJsonsWithSpaces_shouldFillBothCollections() {
        JsonsDiscriminator.of(List.of("{\"OLD_FIELD\": \"OLD_VALUE\"}",
                                      "{\"NEW_FIELD\": \"NEW_VALUE\"}",
                                      "{   \"OLD_FIELD_WITH_SPACES\"   : \n\n\n\"OLD_VALUE_WITH_SPACES\"     } "))
                          .with(List.of("{\"OLD_FIELD\": \"OLD_VALUE\"}",
                                        "{\"OLD_FIELD_WITH_SPACES\": \"OLD_VALUE_WITH_SPACES\"}"),
                                   Object::toString)
                          .into((presentInInitialSet, notPresentInDiscriminationSet) -> {
                                 assertEquals(List.of("{\"OLD_FIELD\": \"OLD_VALUE\"}",
                                                     "{\"OLD_FIELD_WITH_SPACES\": \"OLD_VALUE_WITH_SPACES\"}"),
                                              presentInInitialSet);
                                 assertEquals(List.of("{\"NEW_FIELD\": \"NEW_VALUE\"}"),
                                              notPresentInDiscriminationSet);
                                 return true;
                             });
    }
}
