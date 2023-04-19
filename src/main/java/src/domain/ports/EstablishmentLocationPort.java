package src.domain.ports;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Location;

public interface EstablishmentLocationPort {

    PlacesSearchResponse getNearbyPlaces(
            Location location,
            Integer radius,
            String placeType
    );

}
