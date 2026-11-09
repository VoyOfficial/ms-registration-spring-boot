package br.voy.domain.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PlaceSearchFilterMapper {

    private static final Map<String, String> LABEL_TO_GOOGLE_TYPE =
            Map.of(
                    "Restaurantes", "restaurant",
                    "Cafeteiras", "cafe",
                    "Hotéis", "lodging",
                    "Vida noturna", "night_club",
                    "Lazer", "park",
                    "Esporte", "gym",
                    "Entretenimento", "tourist_attraction");

    private PlaceSearchFilterMapper() {}

    public static Set<String> toGoogleTypes(List<String> filterLabels) {
        if (filterLabels == null || filterLabels.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> googleTypes = new LinkedHashSet<>();
        for (String label : filterLabels) {
            if (label == null || label.isBlank()) {
                continue;
            }
            String mapped = LABEL_TO_GOOGLE_TYPE.get(label.trim());
            if (mapped != null) {
                googleTypes.add(mapped);
            }
        }
        return googleTypes;
    }
}
