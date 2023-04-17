package src.application.providers.adapters;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
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
    public Object getNearbyPlaces(LatLng location) throws IOException, InterruptedException, ApiException {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - LOCATION: {}", location);

        GeoApiContext context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

        var placesResponse = PlacesApi
                .nearbySearchQuery(context, location)
                .radius(5000)
                .language("en")
                .await();

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - LOCATION: {}", location);

        context.shutdown();

        System.out.println(placesResponse);

        return null;

    }

}
