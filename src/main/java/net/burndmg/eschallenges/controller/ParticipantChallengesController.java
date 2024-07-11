package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ParticipantChallengesService;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.dto.participant.ParticipantChallenge;
import net.burndmg.eschallenges.data.dto.participant.ParticipantChallengePage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("challenges")
@RequiredArgsConstructor
public class ParticipantChallengesController {

    private final ParticipantChallengesService challengesService;

    @GetMapping("{id}/run")
    public Mono<ParticipantChallenge> challengeById(@PathVariable String id) {
        return challengesService.getChallengeById(id);
    }

    @GetMapping
    public Mono<ParticipantChallengePage> participantChallenges(PageSettings pageSettings) {
        return challengesService.getChallengeView(pageSettings);
    }
}
