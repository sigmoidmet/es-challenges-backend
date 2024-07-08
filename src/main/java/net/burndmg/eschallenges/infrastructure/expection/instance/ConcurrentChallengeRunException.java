package net.burndmg.eschallenges.infrastructure.expection.instance;

import org.springframework.http.HttpStatus;

public class ConcurrentChallengeRunException extends ApiException {

    public ConcurrentChallengeRunException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS);
    }
}
