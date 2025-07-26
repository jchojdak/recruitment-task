package com.jchojdak.recruitmenttask.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidGitHubTokenException extends RuntimeException {

    public InvalidGitHubTokenException(String message) {
        super(message);
    }
}
