package src.application.controller;

import com.google.maps.model.PlacesSearchResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.domain.entity.Coordinates;
import src.domain.usecase.GetPlaceUseCase;

@Tag(name = "Place", description = "Endpoint with all operations of Place")
@RestController
@RequestMapping("/v1/place")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GetPlaceUseCase getPlaceUseCase;

    @GetMapping()
    public PlacesSearchResponse getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "") String placeType
    ) {

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Latitude: {}, Longitude: {}", latitude, longitude);

        var placesResponse = getPlaceUseCase.getNearbyPlaces(new Coordinates(latitude, longitude), radius, placeType);

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Places Response: {}", placesResponse.results.length);

        return placesResponse;

    }


}
