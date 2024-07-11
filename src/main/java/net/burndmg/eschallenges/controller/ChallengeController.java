package net.burndmg.eschallenges.controller;

import lombok.RequiredArgsConstructor;
import net.burndmg.eschallenges.core.service.ChallengeService;
import net.burndmg.eschallenges.data.dto.ChallengeDto;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("challenges")
@RequiredArgsConstructor
public class ChallengeController {

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
