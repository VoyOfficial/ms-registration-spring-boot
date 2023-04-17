package src.domain.ports;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;

import java.io.IOException;

public interface LocationApiPort {

    public Object getNearbyPlaces(LatLng location) throws IOException, InterruptedException, ApiException;

}
