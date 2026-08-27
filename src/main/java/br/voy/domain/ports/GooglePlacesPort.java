package br.voy.domain.ports;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.PlaceDetails;

public interface GooglePlacesPort {

    NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken);

    PlaceDetails getPlaceDetails(String placeId);

    PlaceDetails getPlaceFromText(String placeName, String city);
}
