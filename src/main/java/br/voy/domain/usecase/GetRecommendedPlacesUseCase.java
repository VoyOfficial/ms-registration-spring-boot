package br.voy.domain.usecase;

import br.voy.application.controller.response.PlaceResponse;
import br.voy.domain.entity.Place;

import java.util.List;

public interface GetRecommendedPlacesUseCase {

    List<PlaceResponse> getRecommendedPlaces(Double latitude, Double longitude, Double range);
}
