package br.voy.domain.exception;

public class RecommendedPlacesNotFoundException extends RuntimeException {

    public static final String MESSAGE_KEY = "error.places.recommendation.not.found";

    public RecommendedPlacesNotFoundException() {
        super(MESSAGE_KEY);
    }
}
