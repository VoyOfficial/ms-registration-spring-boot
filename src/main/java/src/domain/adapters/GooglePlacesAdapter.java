package src.domain.adapters;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.Place;
import src.domain.entity.PlaceDetails;
import src.domain.ports.GooglePlacesPort;
import src.infrastructure.agents.PlacesApiClient;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "services.mock.enable", havingValue = "false")
public class GooglePlacesAdapter implements GooglePlacesPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlacesApiClient placesApiClient;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var response = placesApiClient.searchForNearbyPlaces(latLng, radius, placeTypeEnum, nextPageToken);

        var places = Arrays.stream(response.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        var nearbyPlaces = new NearbyPlaces(places, response.nextPageToken);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Nearby Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

    @Override
    public PlaceDetails getPlaceDetails(String placeId) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE DETAILS - Place Id: {}", placeId);

        var response = placesApiClient.getPlaceDetails(placeId);

        var place = PlaceDetails.toPlaceDetailsByGoogle(response);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH GET PLACE DETAILS - Place: {}", place);

        return place;

    }

    @Override
    public com.google.maps.model.PlaceDetails getPlaceFromText(String placeName, String city) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE FROM TEXT - Place Name: {}, City: {}", placeName, city);

        var response = placesApiClient.getPlaceFromText(placeName, city);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH GET PLACE FROM TEXT - Place Response: {}", response);

        return response;

    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        return Optional.ofNullable(placeType)
                .filter(placeTypeString -> !placeTypeString.isEmpty())
                .map(placeTypeString -> PlaceType.valueOf(placeTypeString.toUpperCase()))
                .orElse(null);

    }

}
