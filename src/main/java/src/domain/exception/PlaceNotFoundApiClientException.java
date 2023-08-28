package src.domain.exception;

public class PlaceNotFoundApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.default.message";

    public PlaceNotFoundApiClientException(String message) {

        super(message);

    }

    public PlaceNotFoundApiClientException(String message, Throwable cause) {

        super(message, cause);

    }

    public PlaceNotFoundApiClientException() {

        super(defaultMessage);

    }

}
