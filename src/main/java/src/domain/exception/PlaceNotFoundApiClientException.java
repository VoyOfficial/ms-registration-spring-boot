package src.domain.exception;

public class PlaceNotFoundApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.not.found.message";

    public PlaceNotFoundApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
