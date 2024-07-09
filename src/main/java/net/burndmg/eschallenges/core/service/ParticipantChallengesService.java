package net.burndmg.eschallenges.core.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.dto.participant.ParticipantChallenge;
import net.burndmg.eschallenges.data.dto.participant.ParticipantChallengePage;
import net.burndmg.eschallenges.data.dto.participant.ParticipantChallengePreview;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ParticipantChallengesService {

    private final ChallengeRepository challengeRepository;

    public Mono<ParticipantChallenge> getChallengeById(String id) {
        return challengeRepository.findById(id, ParticipantChallenge.class)
                                  .switchIfEmpty(Mono.error(new NotFoundException("There is no challenge by id " + id)));
    }

    public Mono<ParticipantChallengePage> getChallengeView(PageSettings pageSettings) {
        return challengeRepository.findAllAfter(pageSettings, ParticipantChallengePreview.class)
                                  .map(challenges -> ParticipantChallengePage.builder()
                                                                             .challenges(challenges)
                                                                             .build());
    }
}
