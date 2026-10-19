package br.voy.infrastructure.sponsor;

import java.util.Locale;

public final class SponsorKeyNormalizer {

    private SponsorKeyNormalizer() {}

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String breakingSpaces =
                value.replace('\u00A0', ' ').replace('\u2007', ' ').replace('\u202F', ' ');
        String collapsed = breakingSpaces.strip().replaceAll("\\s+", " ");
        if (collapsed.isEmpty()) {
            return "";
        }
        return collapsed.toLowerCase(Locale.ROOT);
    }
}
