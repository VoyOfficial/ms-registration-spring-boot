package src.domain.usecase;

import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;

public interface GetNearbyPlacesUseCase {

    NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    );

}
