package net.burndmg.eschallenges.infrastructure.util;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.burndmg.eschallenges.infrastructure.util.JsonUtil.normalizeJson;


@RequiredArgsConstructor(staticName = "of")
public class JsonsDiscriminator<T> {

    private final Collection<String> initialJsons;

    private final List<T> presentInInitialSet = new ArrayList<>();
    private final List<String> notPresentInDiscriminationSet = new ArrayList<>();

    public JsonsDiscriminator<T> with(Collection<? extends T> discriminators, Function<T, String> toJson) {
        Map<String, T> normalizedJsonToDiscriminationValue = discriminators
                .stream()
                .collect(Collectors.toMap(value -> toJson.andThen(JsonUtil::normalizeJson).apply(value),
                                          value -> value));

        for (String initialJson : initialJsons) {
            String normalizedInitialJson = normalizeJson(initialJson);
            T discriminationValue = normalizedJsonToDiscriminationValue.get(normalizedInitialJson);
            if (discriminationValue == null) {
                notPresentInDiscriminationSet.add(initialJson);
            } else {
                presentInInitialSet.add(discriminationValue);
            }
        }

        return this;
    }

    public <R> R into(BiFunction<List<T>, List<String>, R> presentInInitialAndNotPresentInDiscriminationSetsConsumer) {
        return presentInInitialAndNotPresentInDiscriminationSetsConsumer
                .apply(presentInInitialSet, notPresentInDiscriminationSet);
    }
}
