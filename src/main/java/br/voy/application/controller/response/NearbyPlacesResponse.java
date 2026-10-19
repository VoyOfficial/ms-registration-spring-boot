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
        name = "NearbyPlacesResponse",
        description = "Paginated list of nearby or recommended places")
public class NearbyPlacesResponse {

    @Schema(description = "Places for the current page")
    private List<PlaceResponse> places;

    @Schema(
            description = "Opaque token for the next page, null when there is no next page",
            example = "MzoxNzMzNjg5MjQ1Njc4OmFCY0RlRmdIaUprTG1Obw:dGVzdGNoZWM")
    private String nextTokenPage;

    public NearbyPlacesResponse(List<PlaceResponse> placesResponse, String nextTokenPage) {

        this.places = placesResponse;
        this.nextTokenPage = nextTokenPage;
    }
}
