package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.data.dto.PageSettings;
import net.burndmg.eschallenges.data.dto.ParticipantChallenge;
import net.burndmg.eschallenges.data.dto.ParticipantChallengePage;
import net.burndmg.eschallenges.service.ParticipantChallengesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("challenges")
@RequiredArgsConstructor
public class ParticipantChallengesController {

    private final ParticipantChallengesService challengesService;

    @GetMapping("{id}")
    public ParticipantChallenge challengeById(@PathVariable String id) {
        return challengesService.getChallengeById(id);
    }

    @GetMapping
    public ParticipantChallengePage participantChallenges(PageSettings pageSettings) {
        return challengesService.getChallengeView(pageSettings);
    }
}
