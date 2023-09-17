package src.application.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.application.controller.response.NearbyPlacesResponse;
import src.application.controller.response.PlaceResponse;
import src.domain.entity.Coordinates;
import src.domain.exception.StandardError;
import src.domain.usecase.GetNearbyPlacesUseCase;
import src.domain.usecase.GetPlaceDetailsUseCase;

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

    @Operation(summary = "Get 20 nearby Places per time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting 20 Nearby Places ", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(responseCode = "204", description = "error.places.api.nearby.places.zero.results.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "403", description = "error.places.api.request.denied.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "error.places.api.details.invalid.request.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "429", description = "error.places.api.over.query.limit.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
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



    @Operation(summary = "Get Details of a Place by Google ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Getting Details of a Place", content = @Content(schema = @Schema(implementation = PlaceResponse.class))),
            @ApiResponse(responseCode = "204", description = "error.places.api.details.zero.results.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "403", description = "error.places.api.request.denied.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "error.places.api.not.found.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "422", description = "error.places.api.details.invalid.request.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "429", description = "error.places.api.over.query.limit.message", content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    @ResponseStatus(OK)
    @GetMapping("/{placeId}")
    public PlaceResponse getPlaceDetails(
            @PathVariable String placeId
    ) {

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS START - Place Id: {}", placeId);

        var placeDetails = getPlaceDetailsUseCase.getPlaceDetails(placeId);

        var placeDetailsResponse = PlaceResponse.toPlaceDetailsResponse(placeDetails);

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS FINISH - Place Details: {}", placeDetailsResponse);

        return placeDetailsResponse;

    }
}
