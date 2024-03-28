package src.application.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import src.domain.entity.Place;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @Override
    public Place toDomain() {
        return Place
                .builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .city(city)
                .ranking(ranking)
                .build();
    }

}
