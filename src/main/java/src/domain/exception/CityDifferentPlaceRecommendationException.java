package src.domain.exception;

public class CityDifferentPlaceRecommendationException extends RuntimeException{

    private static final String defaultMessage = "place.with.city.different.google.default.message";

    public CityDifferentPlaceRecommendationException(String message) {

        super(message);

    }

    public CityDifferentPlaceRecommendationException() {

        super(defaultMessage);

    }

}
