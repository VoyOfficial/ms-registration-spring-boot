package br.voy.domain.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaginationTokenEncoderTest {

    @Test
    @DisplayName("Should reject unsigned single-part offset tokens")
    void shouldRejectUnsignedSinglePartOffsetTokens() {
        String unsigned =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("100".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> PaginationTokenEncoder.decode(unsigned));
    }

    @Test
    @DisplayName("Should reject unsigned four-part legacy tokens")
    void shouldRejectUnsignedFourPartLegacyTokens() {
        String unsigned =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("20:1:salt:ids".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> PaginationTokenEncoder.decode(unsigned));
    }

    @Test
    @DisplayName("Should round-trip signed tokens with google page token")
    void shouldRoundTripSignedTokens() {
        String encoded = PaginationTokenEncoder.encode(4, Set.of("place-a"), "google-next");

        PaginationTokenEncoder.PaginationState state = PaginationTokenEncoder.decode(encoded);

        assertEquals(4, state.getOffset());
        assertTrue(state.getShownPlaceIds().contains("place-a"));
        assertEquals("google-next", state.getGoogleNextPageToken());
    }

    @Test
    @DisplayName("Should keep null google token when none was encoded")
    void shouldKeepNullGoogleTokenWhenNoneWasEncoded() {
        String encoded = PaginationTokenEncoder.encode(0, Set.of(), null);

        assertNull(PaginationTokenEncoder.decode(encoded).getGoogleNextPageToken());
    }
}
