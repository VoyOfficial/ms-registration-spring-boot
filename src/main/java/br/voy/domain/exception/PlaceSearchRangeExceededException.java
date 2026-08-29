package br.voy.domain.exception;

public class PlaceSearchRangeExceededException extends RuntimeException {

    public static final String MESSAGE_KEY = "error.places.recommendation.out.of.range";

    private final double limitKm;

    public PlaceSearchRangeExceededException(double limitKm) {
        super(MESSAGE_KEY);
        this.limitKm = limitKm;
    }

    public double getLimitKm() {
        return limitKm;
    }
}
