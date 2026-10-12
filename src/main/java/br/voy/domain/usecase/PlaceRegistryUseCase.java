package br.voy.domain.usecase;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.SponsorshipEligibility;

public interface PlaceRegistryUseCase {

    Long registry(Place place, SponsorshipEligibility eligibility);
}
