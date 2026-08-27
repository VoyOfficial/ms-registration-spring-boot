package br.voy.domain.usecase;

import br.voy.application.controller.response.PlaceResponse;
import br.voy.application.controller.response.RecommendedPlacesResponse;
import java.util.List;

public interface GetRecommendedPlacesUseCase {

    List<PlaceResponse> getRecommendedPlaces(Double latitude, Double longitude, Double range);

    RecommendedPlacesResponse getRecommendedPlaces(
            Double latitude,
            Double longitude,
            Double range,
            Integer pageSize,
            String nextPageToken);
}
