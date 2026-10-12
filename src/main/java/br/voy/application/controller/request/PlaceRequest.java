package br.voy.application.controller.request;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.SponsorshipEligibility;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceRequest extends AbstractRequest {

    @Schema(example = "abc123")
    private String googlePlaceId;

    @Schema(example = "Hard Rock Cafe")
    @NotBlank
    private String name;

    @Schema(example = "Gramado")
    @NotBlank
    private String city;

    @Schema(example = "5")
    @NotNull
    private Integer ranking;

    @Schema(
            example = "true",
            description = "Whether the establishment is approved to register as sponsored")
    @NotNull
    private Boolean approved;

    @Schema(
            example = "true",
            description = "Whether the establishment has an active sponsorship subscription")
    @NotNull
    private Boolean subscriptionActive;

    @Schema(example = "2023-11-17T08:30:00Z")
    private LocalDateTime startRecommendation;

    @Override
    public Place toDomain() {
        return Place.builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .city(city)
                .ranking(ranking)
                .startRecommendation(
                        startRecommendation != null ? startRecommendation.toLocalDate() : null)
                .build();
    }

    public SponsorshipEligibility toSponsorshipEligibility() {
        return SponsorshipEligibility.builder()
                .approved(Boolean.TRUE.equals(approved))
                .subscriptionActive(Boolean.TRUE.equals(subscriptionActive))
                .build();
    }
}
