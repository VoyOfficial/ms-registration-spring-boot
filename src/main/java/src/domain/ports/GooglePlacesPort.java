package src.domain.ports;

import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.PlaceDetails;

public interface GooglePlacesPort {

    NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    );

    PlaceDetails getPlaceDetails(String placeId);

    PlaceDetails getPlaceFromText(
            String placeName,
            String city
    );

}
