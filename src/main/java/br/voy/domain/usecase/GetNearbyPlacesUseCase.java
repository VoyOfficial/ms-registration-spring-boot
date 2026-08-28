package br.voy.domain.usecase;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;

public interface GetNearbyPlacesUseCase {

    NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken);
}
