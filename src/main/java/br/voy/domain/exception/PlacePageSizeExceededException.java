package br.voy.domain.exception;

public class PlacePageSizeExceededException extends RuntimeException {

    public static final String MESSAGE_KEY = "error.places.recommendation.page.size.exceeded";

    private final int maxPageSize;

    public PlacePageSizeExceededException(int maxPageSize) {
        super(MESSAGE_KEY);
        this.maxPageSize = maxPageSize;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }
}
