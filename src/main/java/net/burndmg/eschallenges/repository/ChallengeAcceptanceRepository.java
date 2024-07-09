package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface ChallengeAcceptanceRepository extends ReactiveElasticsearchRepository<ChallengeAcceptance, String> {}
