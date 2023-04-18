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
import src.domain.ports.LocationApiPort;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GooglePlacesAPIAdapter implements LocationApiPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${places.api.key}")
    String apiKey;

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            LatLng location,
            Integer radius,
            PlaceType placeType
    ) throws IOException, InterruptedException, ApiException {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Location: {}", location);

        GeoApiContext context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

        var placesSearchRequest = PlacesApi
                .nearbySearchQuery(context, location)
                .radius(radius)
                .language("en");

        if (placeType != null) {
            placesSearchRequest.type(placeType);
        }

        PlacesSearchResponse placesSearchResponse = placesSearchRequest.await();

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Places: {}", placesSearchResponse);

        context.shutdown();

        return placesSearchResponse;

    }

}
