package src.application.providers.adapters;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import src.domain.entity.Location;
import src.domain.ports.EstablishmentLocationPort;
import src.infrastructure.agents.PlacesApiClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GooglePlacesAPIAdapter implements EstablishmentLocationPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlacesApiClient placesApiClient;

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            Location location,
            Integer radius,
            String placeType
    ) {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Location: {}, Radius: {}, PlaceType: {}", location, radius, placeType);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var request = placesApiClient.createNearbySearchRequest(latLng, radius, placeTypeEnum);
        var response = placesApiClient.searchForNearbyPlaces(request);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Places: {}", response);

        return response;

    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        return Optional.ofNullable(placeType)
                .filter(placeTypeString -> !placeTypeString.isEmpty())
                .map(placeTypeString -> PlaceType.valueOf(placeTypeString.toUpperCase()))
                .orElse(null);

    }

}
