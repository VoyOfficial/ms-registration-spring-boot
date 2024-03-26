package src.domain.exception.googlePlaces;

public class RequestDeniedApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.request.denied.message";

    public RequestDeniedApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

    public RequestDeniedApiClientException() {

        super(defaultMessage);

    }

}
