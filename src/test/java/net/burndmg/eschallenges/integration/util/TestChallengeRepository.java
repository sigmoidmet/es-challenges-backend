package net.burndmg.eschallenges.integration.util;

import net.burndmg.eschallenges.data.model.Challenge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TestChallengeRepository extends ElasticsearchRepository<Challenge, String> {}
