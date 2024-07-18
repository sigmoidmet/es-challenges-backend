package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeService;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_CREATION_PRIVILEGE;

@RestController
@RequestMapping("challenges")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + CHALLENGE_CREATION_PRIVILEGE +"')")
public class ChallengeCreationController {

    private final ChallengeService challengeService;

    @PostMapping
    @PutMapping("{id}")
    public Mono<String> save(@PathVariable(required = false) String id, @RequestBody ChallengeDto challenge) {
        return challengeService.save(id, challenge);
    }

    @GetMapping("{id}")
    public Mono<ChallengeDto> findById(@PathVariable String id) {
        return challengeService.findById(id);
    }
}
