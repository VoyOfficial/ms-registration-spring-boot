package src.domain.exception;

public class PlaceInvalidRequestApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.invalid.request.message";

    public PlaceInvalidRequestApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
