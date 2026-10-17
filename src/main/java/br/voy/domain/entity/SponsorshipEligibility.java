package br.voy.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class SponsorshipEligibility {

    private final boolean approved;
    private final boolean subscriptionActive;

    public boolean isEligible() {
        return approved && subscriptionActive;
    }
}
