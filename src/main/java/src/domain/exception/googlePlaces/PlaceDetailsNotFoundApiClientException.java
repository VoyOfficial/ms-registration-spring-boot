package src.domain.exception.googlePlaces;

public class PlaceDetailsNotFoundApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.not.found.message";

    public PlaceDetailsNotFoundApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
