package src.domain.exception.googlePlaces;

public class UnknownErrorApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.unknown.error.message";

    public UnknownErrorApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
