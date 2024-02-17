package src.application.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import src.domain.entity.Place;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceRequest extends AbstractRequest {

    @Schema(example = "abc123")
    private String googlePlaceId;

    @Schema(example = "Hard Rock Cafe")
    private String name;

    @Schema(example = "Gramado")
    private String city;

    @Schema(example = "true")
    private boolean status;

    @Schema(example = "5")
    private Integer ranking;

    @Schema(example = "01/01/2000")
    @NotBlank
    private Date startRecommendation;

    @Schema(example = "01/01/2000")
    @NotBlank
    private Date endRecommendation; // o banco pode preencher automático (startRecommendation + 1 mês)

    @Schema(example = "01/01/2000")
    private Date createdDate;

    @Override
    public Place toDomain() {
        return Place.builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .city(city)
                .status(status)
                .ranking(ranking)
                .startRecommendation(startRecommendation)
                .endRecommendation(endRecommendation)
                .createdDate(createdDate)
                .build();
    }

}
