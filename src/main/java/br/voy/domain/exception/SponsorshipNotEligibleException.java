package br.voy.domain.exception;

public class SponsorshipNotEligibleException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "place.sponsorship.not.eligible.default.message";

    public SponsorshipNotEligibleException(String message) {
        super(message);
    }

    public SponsorshipNotEligibleException() {
        super(DEFAULT_MESSAGE);
    }
}
