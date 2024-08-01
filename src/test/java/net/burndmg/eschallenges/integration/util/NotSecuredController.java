package net.burndmg.eschallenges.integration.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class NotSecuredController {


    @GetMapping("api/not-secured-integration-test-endpoint")
    public Mono<String> test() {
        return Mono.just("You will never see this string");
    }

    @GetMapping("not-secured-and-not-api-integration-test-endpoint")
    public Mono<String> test2() {
        return Mono.just("You will never see this string");
    }
}
