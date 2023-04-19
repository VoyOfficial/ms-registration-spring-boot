package src.domain.ports;

import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Location;

import java.io.IOException;

public interface EstablishmentLocationPort {

    PlacesSearchResponse getNearbyPlaces(
            Location location,
            Integer radius,
            String placeType
    ) throws IOException, InterruptedException, ApiException;

}
