package src.domain.exception.googlePlaces;

public class OverQueryLimitApiClientException extends RuntimeException {

    private static final String defaultMessage = "error.places.api.over.query.limit.message";

    public OverQueryLimitApiClientException(Throwable cause) {

        super(defaultMessage, cause);

    }

}
