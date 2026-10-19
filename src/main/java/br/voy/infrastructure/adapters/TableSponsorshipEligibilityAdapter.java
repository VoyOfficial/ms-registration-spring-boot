package br.voy.infrastructure.adapters;

import br.voy.domain.entity.Sponsor;
import br.voy.domain.entity.SponsorshipEligibility;
import br.voy.domain.ports.SponsorshipEligibilityPort;
import br.voy.domain.repository.SponsorRepository;
import org.springframework.stereotype.Component;

@Component
public class TableSponsorshipEligibilityAdapter implements SponsorshipEligibilityPort {

    private final SponsorRepository sponsorRepository;

    public TableSponsorshipEligibilityAdapter(SponsorRepository sponsorRepository) {
        this.sponsorRepository = sponsorRepository;
    }

    @Override
    public SponsorshipEligibility resolveForRegistration(String name, String city) {
        return sponsorRepository
                .findByNameAndCity(name, city)
                .map(TableSponsorshipEligibilityAdapter::toEligibility)
                .orElseGet(TableSponsorshipEligibilityAdapter::ineligible);
    }

    private static SponsorshipEligibility toEligibility(Sponsor sponsor) {
        return SponsorshipEligibility.builder()
                .approved(sponsor.isApproved())
                .subscriptionActive(sponsor.isSubscriptionActive())
                .build();
    }

    private static SponsorshipEligibility ineligible() {
        return SponsorshipEligibility.builder().approved(false).subscriptionActive(false).build();
    }
}
