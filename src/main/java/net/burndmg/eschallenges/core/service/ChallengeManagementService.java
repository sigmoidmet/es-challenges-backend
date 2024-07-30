package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeResponse;
import net.burndmg.eschallenges.data.dto.UpdateChallengeResponse;
import net.burndmg.eschallenges.data.model.Challenge;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.map.ChallengeMapper;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChallengeManagementService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeMapper challengeMapper;

    public Mono<SaveChallengeResponse> create(ChallengeDto challenge) {
        Challenge model = challengeMapper.toModel(challenge);

        return save(model);
    }

    public Mono<SaveChallengeResponse> update(String id, ChallengeDto challenge) {
        Challenge model = challengeMapper.toModel(id, challenge);

        return save(model);
    }

    private Mono<SaveChallengeResponse> save(Challenge model) {
        return challengeRepository.saveToReadIndex(model)
                                  .flatMap(challengeRepository::saveToUpdateIndex)
                                  .map(Challenge::id)
                                  .map(SaveChallengeResponse::new);
    }

    private Mono<UpdateChallengeResponse> toUpdateResponse(Challenge updatedChallenge) {
        return challengeRepository.isReadAndUpdateAliasesPointToDifferentIndices()
                                  .map(result -> new UpdateChallengeResponse(updatedChallenge.id(), result));
    }

    public Mono<ChallengeDto> findById(String id) {
        return challengeRepository.findById(id, ChallengeDto.class)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }
}
