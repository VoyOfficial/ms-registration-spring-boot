package br.voy.domain.service;

final class PlaceSearchOrdination {

    public static final String MOST_RATED = "Mais avaliados";
    public static final String MOST_COMMENTED = "Mais comentados";
    public static final String CLOSEST = "Mais próximo";

    private PlaceSearchOrdination() {}

    public static String normalize(String ordination) {
        if (ordination == null || ordination.isBlank()) {
            return CLOSEST;
        }
        String trimmed = ordination.trim();
        if (MOST_RATED.equals(trimmed)
                || MOST_COMMENTED.equals(trimmed)
                || CLOSEST.equals(trimmed)) {
            return trimmed;
        }
        return CLOSEST;
    }
}
