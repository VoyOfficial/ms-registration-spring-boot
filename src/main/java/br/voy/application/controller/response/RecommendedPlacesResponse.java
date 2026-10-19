package br.voy.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        name = "RecommendedPlacesResponse",
        description = "Paginated recommended places payload (legacy alias shape)")
public class RecommendedPlacesResponse {

    @Schema(description = "Recommended places for the current page")
    private List<PlaceResponse> places;

    @Schema(
            description = "Opaque token for the next page, null when there is no next page",
            example = "MzoxNzMzNjg5MjQ1Njc4OmFCY0RlRmdIaUprTG1Obw:dGVzdGNoZWM")
    private String nextTokenPage;

    public RecommendedPlacesResponse(List<PlaceResponse> places, String nextTokenPage) {
        this.places = places;
        this.nextTokenPage = nextTokenPage;
    }
}
