package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.map.ChallengeMapper;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMapper challengeMapper;

    public Mono<String> save(@Nullable String id, ChallengeDto challenge) {
        Challenge model = challengeMapper.toModel(id, challenge);

        return challengeRepository.save(model).map(Challenge::id);
    }

    public Mono<ChallengeDto> findById(String id) {
        return challengeRepository.findById(id, ChallengeDto.class)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }
}
