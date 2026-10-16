package br.voy.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FailClosedSponsorshipEligibilityAdapterTest {

    private final FailClosedSponsorshipEligibilityAdapter adapter =
            new FailClosedSponsorshipEligibilityAdapter();

    @Test
    @DisplayName("Should always return ineligible sponsorship for registration")
    void shouldAlwaysReturnIneligibleSponsorshipForRegistration() {
        var eligibility = adapter.resolveForRegistration("Hard Rock Cafe", "Gramado");

        assertFalse(eligibility.isApproved());
        assertFalse(eligibility.isSubscriptionActive());
        assertFalse(eligibility.isEligible());
    }
}
