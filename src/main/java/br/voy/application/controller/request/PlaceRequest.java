package br.voy.application.controller.request;

import br.voy.domain.entity.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "PlaceRequest", description = "Payload to register a sponsored place")
public class PlaceRequest extends AbstractRequest {

    @Schema(description = "Optional Google Place id when already known", example = "abc123")
    private String googlePlaceId;

    @Schema(
            description = "Establishment name used for eligibility lookup",
            example = "Hard Rock Cafe")
    @NotBlank
    private String name;

    @Schema(description = "City used for eligibility lookup", example = "Gramado")
    @NotBlank
    private String city;

    @Schema(description = "Recommendation ranking", example = "5")
    @NotNull
    private Integer ranking;

    @Schema(
            description = "Optional recommendation start timestamp",
            example = "2023-11-17T08:30:00")
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
}
