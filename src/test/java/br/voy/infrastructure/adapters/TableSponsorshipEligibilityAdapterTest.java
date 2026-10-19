package br.voy.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.domain.entity.Sponsor;
import br.voy.domain.repository.SponsorRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TableSponsorshipEligibilityAdapterTest {

    @Mock private SponsorRepository sponsorRepository;

    @InjectMocks private TableSponsorshipEligibilityAdapter adapter;

    @Test
    @DisplayName("Should be eligible when the sponsor is approved and the subscription is active")
    void shouldBeEligibleWhenTheSponsorIsApprovedAndTheSubscriptionIsActive() {
        when(sponsorRepository.findByNameAndCity("Hard Rock Cafe", "Gramado"))
                .thenReturn(
                        Optional.of(
                                Sponsor.builder()
                                        .name("Hard Rock Cafe")
                                        .city("Gramado")
                                        .approved(true)
                                        .subscriptionActive(true)
                                        .build()));

        var eligibility = adapter.resolveForRegistration("Hard Rock Cafe", "Gramado");

        assertTrue(eligibility.isApproved());
        assertTrue(eligibility.isSubscriptionActive());
        assertTrue(eligibility.isEligible());
        verify(sponsorRepository).findByNameAndCity("Hard Rock Cafe", "Gramado");
    }

    @Test
    @DisplayName("Should stay ineligible when the sponsor is missing or not fully active")
    void shouldStayIneligibleWhenTheSponsorIsMissingOrNotFullyActive() {
        when(sponsorRepository.findByNameAndCity("Unknown", "Gramado"))
                .thenReturn(Optional.empty());
        when(sponsorRepository.findByNameAndCity("Cafe Colonial", "Gramado"))
                .thenReturn(
                        Optional.of(
                                Sponsor.builder()
                                        .name("Cafe Colonial")
                                        .city("Gramado")
                                        .approved(false)
                                        .subscriptionActive(true)
                                        .build()));
        when(sponsorRepository.findByNameAndCity("Restaurante X", "Gramado"))
                .thenReturn(
                        Optional.of(
                                Sponsor.builder()
                                        .name("Restaurante X")
                                        .city("Gramado")
                                        .approved(true)
                                        .subscriptionActive(false)
                                        .build()));

        var missing = adapter.resolveForRegistration("Unknown", "Gramado");
        var notApproved = adapter.resolveForRegistration("Cafe Colonial", "Gramado");
        var inactive = adapter.resolveForRegistration("Restaurante X", "Gramado");

        assertFalse(missing.isEligible());
        assertFalse(missing.isApproved());
        assertFalse(missing.isSubscriptionActive());
        assertFalse(notApproved.isEligible());
        assertFalse(notApproved.isApproved());
        assertTrue(notApproved.isSubscriptionActive());
        assertFalse(inactive.isEligible());
        assertTrue(inactive.isApproved());
        assertFalse(inactive.isSubscriptionActive());
    }
}
