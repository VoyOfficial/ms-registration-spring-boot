package src.domain.ports;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Coordinates;

public interface PlacesApiPort {

    PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType
    );

}