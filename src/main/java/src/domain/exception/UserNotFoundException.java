package src.domain.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String defaultMessage = "user.not.found.default.message";

    public UserNotFoundException(String message) {

        super(message);

    }

    public UserNotFoundException() {

        super(defaultMessage);

    }

}
