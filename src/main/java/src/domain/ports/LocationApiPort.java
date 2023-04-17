package src.domain.ports;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;

import java.io.IOException;

public interface LocationApiPort {

    PlacesSearchResponse getNearbyPlaces(LatLng location, Integer radius) throws IOException, InterruptedException, ApiException;

}
