package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.model.ChallengeAcceptance;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChallengeAcceptanceRepository extends ElasticsearchRepository<ChallengeAcceptance, String> {}
