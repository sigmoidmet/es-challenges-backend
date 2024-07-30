package net.burndmg.eschallenges.repository.common;

import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IndexRepositoryImpl implements IndexRepository {

    private final ReactiveElasticsearchClient elasticsearchOperations;

    @Override
    public Mono<Set<String>> findAliases(IndexCoordinates indexCoordinates) {
        return elasticsearchOperations.indices()
                                      .getAlias(GetAliasRequest.of(builder -> builder.index(indexCoordinates.getIndexName())))
                                      .map(aliasResponse -> aliasResponse.result()
                                                                         .values()
                                                                         .stream()
                                                                         .flatMap(aliases -> aliases.aliases().keySet().stream())
                                                                         .collect(Collectors.toSet()));
    }
}
