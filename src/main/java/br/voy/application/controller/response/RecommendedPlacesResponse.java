package br.voy.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendedPlacesResponse {

    private List<PlaceResponse> places;
    private String nextTokenPage;

    public RecommendedPlacesResponse(List<PlaceResponse> places, String nextTokenPage) {
        this.places = places;
        this.nextTokenPage = nextTokenPage;
    }
}

