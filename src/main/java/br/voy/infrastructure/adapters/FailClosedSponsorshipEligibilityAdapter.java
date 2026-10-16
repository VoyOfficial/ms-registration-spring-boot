package br.voy.infrastructure.adapters;

import br.voy.domain.entity.SponsorshipEligibility;
import br.voy.domain.ports.SponsorshipEligibilityPort;
import org.springframework.stereotype.Component;

@Component
public class FailClosedSponsorshipEligibilityAdapter implements SponsorshipEligibilityPort {

    @Override
    public SponsorshipEligibility resolveForRegistration(String name, String city) {
        return SponsorshipEligibility.builder().approved(false).subscriptionActive(false).build();
    }
}
