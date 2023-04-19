package src.infrastructure.agents;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PlacesApiClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GeoApiContext context;

    public PlacesApiClient(
            @Value("${places.api.key}")
            String apiKey
    ) {

        this.context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

    }

    public NearbySearchRequest createNearbySearchRequest(LatLng latLng, Integer radius, PlaceType placeType) {

        logger.info("PLACES API CLIENT - Create Nearby Search Request");

        return PlacesApi.nearbySearchQuery(context, latLng)
                .language("en")
                .radius(radius)
                .type(placeType);

    }

    public PlacesSearchResponse searchForNearbyPlaces(NearbySearchRequest request) {

        try {

            logger.info("PLACES API CLIENT - Search For Nearby Places");

            return request.await();

        } catch (Exception exception) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Occured an Error: {}", exception.getMessage());

            throw new RuntimeException("Error searching for places", exception);

        } finally {
            context.shutdown();
        }

    }


}