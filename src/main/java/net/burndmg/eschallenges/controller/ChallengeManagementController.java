package net.burndmg.eschallenges.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeManagementService;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeDto;
import net.burndmg.eschallenges.data.dto.SaveChallengeResponse;
import net.burndmg.eschallenges.infrastructure.annotation.AuthUsername;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static net.burndmg.eschallenges.infrastructure.config.security.SecurityAuthority.CHALLENGE_MANAGEMENT_PRIVILEGE;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/challenges")
@PreAuthorize("hasAuthority('" + CHALLENGE_MANAGEMENT_PRIVILEGE + "')")
public class ChallengeManagementController {

    private final ChallengeManagementService challengeManagementService;

    @PostMapping
    public Mono<SaveChallengeResponse> create(@Valid @RequestBody SaveChallengeDto challenge, @AuthUsername String username) {
        return challengeManagementService.create(challenge, username);
    }


    @PutMapping("{id}")
    public Mono<SaveChallengeResponse> update(@PathVariable String id,
                                              @Valid @RequestBody SaveChallengeDto challenge,
                                              @AuthUsername String username) {
        return challengeManagementService.update(id, challenge, username);
    }

    @GetMapping("{id}")
    public Mono<ChallengeDto> findById(@PathVariable String id) {
        return challengeManagementService.findById(id);
    }
}
