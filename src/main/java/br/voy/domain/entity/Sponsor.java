package br.voy.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Sponsor {

    private final String name;
    private final String city;
    private final boolean approved;
    private final boolean subscriptionActive;
}
