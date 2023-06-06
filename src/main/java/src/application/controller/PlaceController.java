package src.application.controller;

import com.google.maps.model.PlaceDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.application.controller.response.NearbyPlacesResponse;
import src.application.controller.response.PlaceResponse;
import src.domain.entity.Coordinates;
import src.domain.usecase.GetNearbyPlacesUseCase;
import src.domain.usecase.GetPlaceDetailsUseCase;

import java.util.stream.Collectors;

@Tag(name = "Place", description = "Endpoint with all operations of Place")
@RestController
@RequestMapping("/v1/places")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GetNearbyPlacesUseCase getNearbyPlacesUseCase;

    @Autowired
    private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @GetMapping()
    public ResponseEntity<NearbyPlacesResponse> getNearbyPlaces(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "") String placeType,
            @RequestParam(defaultValue = "") String nextPageToken
    ) {

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES START - Latitude: {}, Longitude: {}", latitude, longitude);

        var nearbyPlaces = getNearbyPlacesUseCase.getNearbyPlaces(
                new Coordinates(latitude, longitude),
                radius,
                placeType,
                nextPageToken
        );

        var placeResponses = nearbyPlaces.getPlaces()
                .stream()
                .map(PlaceResponse::new)
                .collect(Collectors.toList());

        var nearbyPlacesResponse = new NearbyPlacesResponse(placeResponses, nearbyPlaces.getNextTokenPage());

        logger.info("PLACE CONTROLLER - GET NEARBY PLACES FINISH - Places Response: {}", nearbyPlacesResponse);

        return ResponseEntity.ok(nearbyPlacesResponse);

    }

    @GetMapping("/{placeId}")
    public PlaceDetails getPlaceDetails(
            @PathVariable String placeId
    ) {

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS START - Place Id: {}", placeId);

        var placeDetails = getPlaceDetailsUseCase.getPlaceDetails(placeId);

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS FINISH - Place Details: {}", placeDetails);

        return placeDetails;

    }

}
