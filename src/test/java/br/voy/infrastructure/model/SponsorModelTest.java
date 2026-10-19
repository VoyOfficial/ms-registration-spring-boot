package br.voy.infrastructure.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SponsorModelTest {

    @Test
    @DisplayName("Should normalize sponsor keys before persisting")
    void shouldNormalizeSponsorKeysBeforePersisting() {
        var model =
                SponsorModel.builder()
                        .name("  Hard   Rock Cafe ")
                        .city(" Gramado ")
                        .approved(true)
                        .subscriptionActive(true)
                        .build();

        model.applyKeys();

        assertEquals("hard rock cafe", model.getNameKey());
        assertEquals("gramado", model.getCityKey());
        assertNotNull(model.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map sponsor flags to the domain entity")
    void shouldMapSponsorFlagsToTheDomainEntity() {
        var model =
                SponsorModel.builder()
                        .name("Hard Rock Cafe")
                        .city("Gramado")
                        .approved(true)
                        .subscriptionActive(false)
                        .build();

        var sponsor = model.toDomain();

        assertEquals("Hard Rock Cafe", sponsor.getName());
        assertEquals("Gramado", sponsor.getCity());
        assertTrue(sponsor.isApproved());
        assertFalse(sponsor.isSubscriptionActive());
    }
}
