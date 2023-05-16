package src.domain.usecase;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Coordinates;

public interface GetNearbyPlacesUseCase {

    PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    );

}