package net.burndmg.eschallenges.service;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.dto.ParticipantChallenge;
import net.burndmg.eschallenges.data.dto.ParticipantChallengePage;
import net.burndmg.eschallenges.data.dto.ParticipantChallengePreview;
import net.burndmg.eschallenges.infrastructure.expection.instance.NotFoundException;
import net.burndmg.eschallenges.repository.ChallengeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantChallengesService {

    private final ChallengeRepository challengeRepository;

    public ParticipantChallenge getChallengeById(String id) {

        return challengeRepository.findById(id, ParticipantChallenge.class)
                                  .orElseThrow(() -> new NotFoundException("There is no challenge by id " + id));
    }

    public ParticipantChallengePage getChallengeView(PageSettings pageSettings) {
        return ParticipantChallengePage
                .builder()
                .challenges(challengeRepository.findAllAfter(pageSettings, ParticipantChallengePreview.class))
                .build();
    }
}
