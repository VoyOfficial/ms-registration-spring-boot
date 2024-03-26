package src.application.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import src.domain.entity.Place;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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

    @Schema(example = "01/01/2000")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate endRecommendation; // o banco pode preencher automático (startRecommendation + 1 mês)

    @Override
    public Place toDomain() {
        return Place.builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .city(city)
                .ranking(ranking)
                .endRecommendation(endRecommendation)
                .build();
    }

}
