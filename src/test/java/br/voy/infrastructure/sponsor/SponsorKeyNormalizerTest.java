package br.voy.infrastructure.sponsor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SponsorKeyNormalizerTest {

    @Test
    @DisplayName("Should collapse case and whitespace into a sponsor lookup key")
    void shouldCollapseCaseAndWhitespaceIntoASponsorLookupKey() {
        assertEquals("hard rock cafe", SponsorKeyNormalizer.normalize("  Hard   Rock   Cafe  "));
        assertEquals("gramado", SponsorKeyNormalizer.normalize("Gramado"));
        assertEquals("são paulo", SponsorKeyNormalizer.normalize("SÃO PAULO"));
    }

    @Test
    @DisplayName("Should treat blank sponsor keys as empty")
    void shouldTreatBlankSponsorKeysAsEmpty() {
        assertEquals("", SponsorKeyNormalizer.normalize(null));
        assertEquals("", SponsorKeyNormalizer.normalize("   "));
    }
}
