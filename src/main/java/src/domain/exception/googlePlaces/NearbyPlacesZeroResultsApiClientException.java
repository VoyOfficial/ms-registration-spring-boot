package src.domain.exception.googlePlaces;

public class NearbyPlacesZeroResultsApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.nearby.search.zero.results.message";

    public NearbyPlacesZeroResultsApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
