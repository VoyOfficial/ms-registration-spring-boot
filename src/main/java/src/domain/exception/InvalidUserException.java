package src.domain.exception;

public class InvalidUserException extends RuntimeException {

    private static final String defaultMessage = "{invalid.user.default.message}";

    public InvalidUserException(String message) {

        super(message);

    }

    public InvalidUserException() {

        super(defaultMessage);

    }

}
