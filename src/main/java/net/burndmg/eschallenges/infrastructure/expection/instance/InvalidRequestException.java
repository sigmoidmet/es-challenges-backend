package net.burndmg.eschallenges.infrastructure.expection.instance;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class InvalidRequestException extends ApiException {

    public InvalidRequestException(String message) {
        super(message, BAD_REQUEST);
    }
}
