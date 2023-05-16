package src.application.controller;

import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import src.domain.entity.Coordinates;
import src.domain.usecase.GetNearbyPlacesUseCase;
import src.domain.usecase.GetPlaceDetailsUseCase;

@Tag(name = "Place", description = "Endpoint with all operations of Place")
@RestController
@RequestMapping("/v1/place")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GetNearbyPlacesUseCase getNearbyPlacesUseCase;

    @Autowired
    private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @GetMapping()
    public PlacesSearchResponse getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "") String placeType,
            @RequestParam(defaultValue = "") String nextPageToken
    ) {

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Latitude: {}, Longitude: {}", latitude, longitude);

        var placeDetailsReponseList = getNearbyPlacesUseCase
                .getNearbyPlaces(
                        new Coordinates(latitude, longitude),
                        radius,
                        placeType,
                        nextPageToken
                );

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES - Places Response: {}", placeDetailsReponseList);

        return placeDetailsReponseList;

    }

    @GetMapping("/{placeId}")
    public PlaceDetails getPlaceDetails(
            @PathVariable String placeId
    ) {

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS - Place Id: {}", placeId);

        var placeDetails = getPlaceDetailsUseCase.getPlaceDetails(placeId);

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS - Place Details: {}", placeDetails);

        return placeDetails;

    }

}
