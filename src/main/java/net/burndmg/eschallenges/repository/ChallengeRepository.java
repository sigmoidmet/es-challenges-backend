package net.burndmg.eschallenges.repository;

import net.burndmg.eschallenges.data.dto.Page;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.model.Challenge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ChallengeRepository extends ElasticsearchRepository<Challenge, String>, PaginationRepository<Challenge> {

    default Page<Challenge> findAllAfter(PageSettings pageSettings) {
        return findAllAfter(pageSettings, Challenge.class);
    }
}
