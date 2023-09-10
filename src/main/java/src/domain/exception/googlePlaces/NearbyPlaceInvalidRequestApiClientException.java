package src.domain.exception.googlePlaces;

public class NearbyPlaceInvalidRequestApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.nearby.place.invalid.request.message";

    public NearbyPlaceInvalidRequestApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
