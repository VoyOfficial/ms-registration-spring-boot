package src.domain.usecase;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Coordinates;

public interface GetPlaceUseCase {

    PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType
    );

}
