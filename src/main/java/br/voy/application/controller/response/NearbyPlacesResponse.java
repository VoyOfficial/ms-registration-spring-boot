package br.voy.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NearbyPlacesResponse {

    private List<PlaceResponse> places;
    private String nextTokenPage;

    public NearbyPlacesResponse(List<PlaceResponse> placesResponse, String nextTokenPage) {

        this.places = placesResponse;
        this.nextTokenPage = nextTokenPage;
    }
}
