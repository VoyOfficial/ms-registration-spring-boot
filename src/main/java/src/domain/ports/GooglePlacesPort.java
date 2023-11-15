package src.domain.ports;

import com.google.maps.model.PlaceDetails;
import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.Place;

public interface GooglePlacesPort {

    NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    );

    Place getPlaceDetails(String placeId);

    PlaceDetails getPlaceFromText(
            String placeName,
            String city
    );

}
