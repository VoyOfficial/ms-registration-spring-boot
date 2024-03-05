package src.domain.exception;

public class PlaceAlreadyExistsException extends RuntimeException {
    private static final String defaultMessage = "place.already.exists.default.message";

    public PlaceAlreadyExistsException(String message) {

        super(message);

    }

    public PlaceAlreadyExistsException() {

        super(defaultMessage);

    }
}
