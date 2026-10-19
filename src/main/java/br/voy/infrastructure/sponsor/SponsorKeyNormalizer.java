package br.voy.infrastructure.sponsor;

import java.util.Locale;

public final class SponsorKeyNormalizer {

    private SponsorKeyNormalizer() {}

    public static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
