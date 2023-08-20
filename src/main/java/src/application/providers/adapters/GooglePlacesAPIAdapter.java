package src.application.providers.adapters;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import src.domain.entity.Coordinates;
import src.domain.ports.PlacesApiPort;
import src.infrastructure.agents.PlacesApiClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "services.mock.enable", havingValue = "false")
public class GooglePlacesAPIAdapter implements PlacesApiPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlacesApiClient placesApiClient;

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var request = placesApiClient.createNearbySearchRequest(latLng, radius, placeTypeEnum, nextPageToken);
        var response = placesApiClient.searchForNearbyPlaces(request);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Places: {}", response);

        return response;

    }

    @Override
    public PlaceDetails getPlaceDetails(
            String placeId
    ) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE DETAILS - Place Id: {}", placeId);

        var request = placesApiClient.createPlaceDetailsRequest(placeId);
        var response = placesApiClient.getPlaceDetails(request);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH GET PLACE DETAILS - Place Id: {}", response);

        return response;

    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        return Optional.ofNullable(placeType)
                .filter(placeTypeString -> !placeTypeString.isEmpty())
                .map(placeTypeString -> PlaceType.valueOf(placeTypeString.toUpperCase()))
                .orElse(null);

    }

}
