package src.domain.exception.googlePlaces;

public class PlaceDetailsZeroResultsApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.details.zero.results.message";

    public PlaceDetailsZeroResultsApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

    public PlaceDetailsZeroResultsApiClientException() {

        super(defaultMessage);

    }

}
