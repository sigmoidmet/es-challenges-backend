package net.burndmg.eschallenges.infrastructure.expection.instance;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message, NOT_FOUND);
    }
}
