package net.burndmg.eschallenges.integration.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class NotSecuredController {


    @GetMapping("not-secured-integration-test-endpoint")
    public Mono<String> test() {
        return Mono.just("You will never see this string");
    }
}
