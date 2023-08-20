package src.domain.mocks;

import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import src.domain.entity.Coordinates;
import src.domain.ports.PlacesApiPort;

@Component
@ConditionalOnProperty(name = "services.mock.enable", havingValue = "true")
public class GooglePlacesAPIAdapterMock implements PlacesApiPort {

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken) {

        return null;

    }

    @Override
    public PlaceDetails getPlaceDetails(String placeId) {
        return null;
    }

}
