package br.voy.domain.exception;

import org.springframework.beans.factory.annotation.Value;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {

        super(message);

    }
}
