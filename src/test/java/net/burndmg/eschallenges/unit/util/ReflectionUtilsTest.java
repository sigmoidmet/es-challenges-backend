package net.burndmg.eschallenges.unit.util;

import net.burndmg.eschallenges.infrastructure.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class ReflectionUtilsTest {

    @Test
    void getNonStaticFieldsNames() {
        //noinspection unused
        record TestRecord(String field1, int field2) {
            public static final String NON_INCLUDED_FIELD = "haha";
        }

        assertThat(ReflectionUtils.getNonStaticFieldsNames(TestRecord.class))
                .containsExactlyInAnyOrder("field1", "field2");
    }


    @Test
    void name() {
        Integer block = method().block();
        System.out.println(block);
    }

    private static Mono<Integer> method() {
        return Mono.just(1)
                .flatMap(i -> Mono.when())
                .then(Mono.defer(() -> Mono.just(2)));
    }
}
