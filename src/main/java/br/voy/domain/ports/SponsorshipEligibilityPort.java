package br.voy.domain.ports;

import br.voy.domain.entity.SponsorshipEligibility;

public interface SponsorshipEligibilityPort {

    SponsorshipEligibility resolveForRegistration(String name, String city);
}
