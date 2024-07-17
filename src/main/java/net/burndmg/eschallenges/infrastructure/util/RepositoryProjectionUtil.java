package net.burndmg.eschallenges.infrastructure.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class RepositoryProjectionUtil {

    public static <T> NativeQueryBuilder queryBuilderWithProjectionFor(Class<T> projectionType) {
        String[] includedFields = ReflectionUtils.getNonStaticFieldsNames(projectionType);

        return NativeQuery.builder()
                          .withSourceFilter(FetchSourceFilter.of(builder -> builder.withIncludes(includedFields)));
    }

    public static <T> List<String> sourceIncludes(Class<T> projectionType) {
        return Arrays.stream(ReflectionUtils.getNonStaticFieldsNames(projectionType)).toList();
    }
}
