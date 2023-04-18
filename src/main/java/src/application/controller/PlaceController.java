package src.application.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.domain.ports.LocationApiPort;

import java.io.IOException;

@Tag(name = "Place", description = "Endpoint with all operations of Place")
@RestController
@RequestMapping("/v1/place")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LocationApiPort locationApiPort;

    @GetMapping()
    public PlacesSearchResponse getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "") String placeType
    ) throws IOException, InterruptedException, ApiException {

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Latitude: {}, Longitude: {}", latitude, longitude);

        var location = new LatLng(latitude, longitude);

        PlaceType placeTypeEnum = null;

        if (!placeType.isEmpty()) {
            placeTypeEnum = PlaceType.valueOf(placeType.toUpperCase());
        }

        var placesResponse = locationApiPort.getNearbyPlaces(location, radius, placeTypeEnum);

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Places Response: {}", placesResponse.results.length);

        return placesResponse;

    }


}
