package br.voy.application.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import br.voy.domain.entity.Place;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

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

    @Schema(example = "2023-11-17T08:30:00Z")
    private LocalDateTime startRecommendation;

    @Override
    public Place toDomain() {
        return Place
                .builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .city(city)
                .ranking(ranking)
                .startRecommendation(startRecommendation.toLocalDate())
                .build();
    }

}
