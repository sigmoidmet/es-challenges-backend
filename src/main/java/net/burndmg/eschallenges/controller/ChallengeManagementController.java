package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeManagementService;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.dto.CreateChallengeResponse;
import net.burndmg.eschallenges.data.dto.UpdateChallengeResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_CREATION_PRIVILEGE;

@RestController
@RequestMapping("challenges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + CHALLENGE_CREATION_PRIVILEGE +"')")
public class ChallengeManagementController {

    private final ChallengeManagementService challengeManagementService;

    @PostMapping
    public Mono<CreateChallengeResponse> create(@RequestBody ChallengeDto challenge) {
        return challengeManagementService.create(challenge);
    }


    @PutMapping("{id}")
    public Mono<UpdateChallengeResponse> update(@PathVariable String id, @RequestBody ChallengeDto challenge) {
        return challengeManagementService.update(id, challenge);
    }

    @GetMapping("{id}")
    public Mono<ChallengeDto> findById(@PathVariable String id) {
        return challengeManagementService.findById(id);
    }
}
