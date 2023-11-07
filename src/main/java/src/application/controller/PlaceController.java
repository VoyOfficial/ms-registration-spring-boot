package src.application.controller;

import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.application.controller.request.PlaceRequest;
import src.application.controller.response.NearbyPlacesResponse;
import src.application.controller.response.PlaceResponse;
import src.domain.entity.Coordinates;
import src.domain.exception.StandardError;
import src.domain.usecase.GetNearbyPlacesUseCase;
import src.domain.usecase.GetPlaceDetailsUseCase;
import src.domain.usecase.GetRecommendationPlacesUseCase;
import src.domain.usecase.PlaceRegistryUseCase;

import javax.validation.Valid;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Place", description = "Endpoint with all operations of Places")
@RestController
@RequestMapping("/v1/places")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GetNearbyPlacesUseCase getNearbyPlacesUseCase;

    @Autowired
    private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @Autowired
    private GetRecommendationPlacesUseCase getRecommendationPlacesUseCase;

    @Autowired
    private PlaceRegistryUseCase placeRegistryUseCase;

    @Operation(summary = "Get 20 nearby Places per time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting 20 Nearby Places ", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @ResponseStatus(OK)
    @GetMapping()
    public ResponseEntity<NearbyPlacesResponse> getNearbyPlaces(
            @Schema(example = "-29.366054", type = "Double")
            @RequestParam Double latitude,
            @Schema(example = "-50.877113", type = "Double")
            @RequestParam Double longitude,
            @Schema(example = "5000", type = "Integer")
            @RequestParam(defaultValue = "5000") Integer radius,
            @Schema(example = "shopping_mall", type = "String", implementation = PlaceType.class)
            @RequestParam(defaultValue = "") String placeType,
            @Schema(example = "AZose0kJX6a...", type = "String", description = "Token for to get next page of 20 nearby places")
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
                .map(PlaceResponse::toNearbyPlaceResponse)
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

    @Operation(summary = "Get 5 recommendations place by location")
    @GetMapping("/recommendations")
    public Object getRecommendationsPlaces(@RequestParam double latitude, @RequestParam double longitude) throws IOException, InterruptedException, ApiException {
        return getRecommendationPlacesUseCase.getRecommendationPlaces(latitude, longitude);
    }

    @Operation(summary = "Create a new sponsor place in the database")
    @PostMapping("/recommendations/new")
    public ResponseEntity createNewRecommendationPlace(@RequestBody @Valid PlaceRequest placeProperties) {
        placeRegistryUseCase.registry(placeProperties.toDomain());

        return ResponseEntity.status(OK).build();
    }

}
