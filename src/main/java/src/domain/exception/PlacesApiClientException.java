package src.domain.exception;

public class PlacesApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.default.message";

    public PlacesApiClientException(String message, Throwable cause) {

        super(message, cause);

    }

}
