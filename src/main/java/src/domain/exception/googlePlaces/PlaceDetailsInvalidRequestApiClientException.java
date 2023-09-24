package src.domain.exception.googlePlaces;

public class PlaceDetailsInvalidRequestApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.details.invalid.request.message";

    public PlaceDetailsInvalidRequestApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
