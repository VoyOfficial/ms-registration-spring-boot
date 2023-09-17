package src.domain.exception.googlePlaces;

public class PlacesApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.default.message";

    public PlacesApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
