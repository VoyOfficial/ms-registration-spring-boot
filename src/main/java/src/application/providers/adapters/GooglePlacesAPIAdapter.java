package src.application.providers.adapters;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.domain.entity.Location;
import src.domain.ports.EstablishmentLocationPort;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GooglePlacesAPIAdapter implements EstablishmentLocationPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${places.api.key}")
    String apiKey;

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            Location location,
            Integer radius,
            String placeType
    ) throws IOException, InterruptedException, ApiException {

        GeoApiContext context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Location: {}, Radius: {}, PlaceType: {}", location, radius, placeType);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var placesSearchRequest = PlacesApi
                .nearbySearchQuery(context, latLng)
                .language("en")
                .radius(radius)
                .type(placeTypeEnum);

        logger.info("GOOGLE PLACES API ADAPTER - CALLING PLACES API NEARBY SEARCH");
        PlacesSearchResponse placesSearchResponse = placesSearchRequest.await();

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Places: {}", placesSearchResponse);

        context.shutdown();

        return placesSearchResponse;

    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        if (!placeType.isEmpty()) {
            return PlaceType.valueOf(placeType.toUpperCase());
        }

        return null;

    }

}
