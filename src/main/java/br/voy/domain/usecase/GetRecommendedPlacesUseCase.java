package br.voy.domain.usecase;

import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import java.util.List;

public interface GetRecommendedPlacesUseCase {

    List<Place> getRecommendedPlaces(Double latitude, Double longitude, Double range);

    NearbyPlaces getRecommendedPlaces(
            Double latitude,
            Double longitude,
            Double range,
            Integer pageSize,
            String nextPageToken);
}
