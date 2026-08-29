package br.voy.application.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import br.voy.application.controller.request.PlaceRequest;
import br.voy.application.controller.response.DefaultResponse;
import br.voy.application.controller.response.NearbyPlacesResponse;
import br.voy.application.controller.response.PlaceDetailsResponse;
import br.voy.application.controller.response.PlaceResponse;
import br.voy.domain.entity.Coordinates;
import br.voy.domain.exception.PlaceAlreadyExistsException;
import br.voy.domain.exception.StandardError;
import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import br.voy.domain.usecase.GetPlaceDetailsUseCase;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.domain.usecase.PlaceRegistryUseCase;
import com.google.maps.model.PlaceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Place", description = "Endpoint with all operations of Places")
@RestController
@RequestMapping("/v1/places")
public class PlaceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${error.places.recommendation.status404.message}")
    private String RECOMMENDATION_PLACE_NOT_FOUND_MESSAGE;

    @Autowired private GetNearbyPlacesUseCase getNearbyPlacesUseCase;

    @Autowired private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @Autowired private PlaceRegistryUseCase placeRegistryUseCase;

    @Autowired private GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @Operation(summary = "Get 20 nearby Places per time")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Getting 20 Nearby Places ",
                        content =
                                @Content(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                NearbyPlacesResponse.class))),
                @ApiResponse(
                        responseCode = "204",
                        description = "error.places.api.nearby.places.zero.results.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "error.places.api.request.denied.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "422",
                        description = "error.places.api.details.invalid.request.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "429",
                        description = "error.places.api.over.query.limit.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    @ResponseStatus(OK)
    @GetMapping()
    public ResponseEntity<NearbyPlacesResponse> getNearbyPlaces(
            @Parameter(description = "User's latitude(required)", required = true)
                    @Schema(example = "-29.366054", type = "Double")
                    @RequestParam
                    Double latitude,
            @Parameter(description = "User's longitude(required)", required = true)
                    @Schema(example = "-50.877113", type = "Double")
                    @RequestParam
                    Double longitude,
            @Parameter(description = "radius size for search(required)", required = true)
                    @Schema(example = "5000", type = "Integer")
                    @RequestParam(defaultValue = "5000")
                    Integer radius,
            @Parameter(description = "place type (required)", required = true)
                    @Schema(
                            example = "shopping_mall",
                            type = "String",
                            implementation = PlaceType.class)
                    @RequestParam(defaultValue = "")
                    String placeType,
            @Parameter(description = "next page token (required)", required = true)
                    @Schema(
                            example = "AZose0kJX6a...",
                            type = "String",
                            description = "Token for to get next page of 20 nearby places")
                    @RequestParam(defaultValue = "")
                    String nextPageToken) {

        logger.info(
                "PLACE CONTROLLER - GET NEARBY PLACES START - Latitude: {}, Longitude: {}",
                latitude,
                longitude);

        var nearbyPlaces =
                getNearbyPlacesUseCase.getNearbyPlaces(
                        new Coordinates(latitude, longitude), radius, placeType, nextPageToken);

        var placeResponses =
                nearbyPlaces.getPlaces().stream()
                        .map(PlaceResponse::fromDomain)
                        .collect(Collectors.toList());

        var nearbyPlacesResponse =
                new NearbyPlacesResponse(placeResponses, nearbyPlaces.getNextTokenPage());

        logger.info(
                "PLACE CONTROLLER - GET NEARBY PLACES FINISH - Nearby Places Response: {}",
                nearbyPlacesResponse);

        return ResponseEntity.ok(nearbyPlacesResponse);
    }

    @Operation(summary = "Get Details of a Place by Google ID")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Getting Details of a Place",
                        content =
                                @Content(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                PlaceDetailsResponse.class))),
                @ApiResponse(
                        responseCode = "204",
                        description = "error.places.api.details.zero.results.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "error.places.api.request.denied.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "error.places.api.not.found.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "422",
                        description = "error.places.api.details.invalid.request.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "429",
                        description = "error.places.api.over.query.limit.message",
                        content = @Content(schema = @Schema(implementation = StandardError.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    @ResponseStatus(OK)
    @GetMapping("/{placeId}")
    public PlaceDetailsResponse getPlaceDetails(
            @Parameter(description = "Google Place Id", required = true)
                    @Schema(example = "ChIJPQmNhEMyGZURxuHk44vIaIw", type = "String")
                    @PathVariable
                    String placeId) {

        logger.info("PLACE CONTROLLER - GET PLACE DETAILS START - Place Id: {}", placeId);

        var placeDetails = getPlaceDetailsUseCase.getPlaceDetails(placeId);

        var placeDetailsResponse = PlaceDetailsResponse.toPlaceDetailsResponse(placeDetails);

        logger.info(
                "PLACE CONTROLLER - GET PLACE DETAILS FINISH - Place Details: {}",
                placeDetailsResponse);

        return placeDetailsResponse;
    }

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Registry place",
                        content = @Content,
                        headers =
                                @Header(
                                        name = "Location",
                                        description = "Url to access the created resource")),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                PlaceAlreadyExistsException
                                                                        .class))),
                @ApiResponse(
                        responseCode = "422",
                        description = "Unprocessable Entity",
                        content =
                                @Content(
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                PlaceAlreadyExistsException
                                                                        .class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content = @Content(schema = @Schema(implementation = StandardError.class)))
            })
    @ResponseStatus(CREATED)
    @PostMapping
    @Operation(summary = "Registry recommendations place")
    public ResponseEntity<Long> createPlaceRecommendation(
            @Parameter(description = "Request para salvar o local") @RequestBody @Valid
                    PlaceRequest request,
            UriComponentsBuilder uriBuilder) {

        logger.info("PLACE CONTROLLER - REGISTRY - Place: {}", request.getName());

        var placeId = placeRegistryUseCase.registry(request.toDomain());

        var uri = uriBuilder.path("/v1/places/{place}").buildAndExpand(placeId).toUri();

        logger.info("PLACE CONTROLLER - REGISTERED PLACE - Place: {}", placeId);

        return ResponseEntity.created(uri).build();
    }

    @Operation(
            summary = "Get recommended places",
            description =
                    "Returns up to 5 places closest to the given coordinates. Latitude and longitude are required.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista de lugares obtida com sucesso",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = DefaultResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                    {
                        "message": "ok",
                        "data": [
                            {
                                "id": 12,
                                "googlePlaceId": "ChIJsdUuyRc1GZURg7Hy1kfaAeU",
                                "name": "Space Adventure Canela",
                                "contact": "(54) 3286-1055",
                                "address": "Av. Ernani Kroeff Fleck, 960 - Vila Suica, Canela - RS, 95684-180",
                                "city": "Canela",
                                "ranking": 1,
                                "latitude": -29.35963,
                                "longitude": -50.83543,
                                "distanceFromUserLocation": "1.224 km"
                            },
                            {
                                "id": 8,
                                "googlePlaceId": "ChIJ-ZdZYBc1GZURaaRRs7WmIAg",
                                "name": "Belvedere Vale do Quilombo",
                                "contact": "(54) 3286-4054",
                                "address": "Av. das Hortênsias, 2536 - Vila Suica, Gramado - RS, 95670-000",
                                "city": "Gramado",
                                "ranking": 2,
                                "latitude": -29.37812,
                                "longitude": -50.86689,
                                "distanceFromUserLocation": "2.723 km"
                            }
                        ]
                    }
                """))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = DefaultResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                    {
                        "message": "Recommended places not found"
                    }
                """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = DefaultResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "Formato inválido",
                                                    value =
                                                            """
                        {
                            "message": "latitude e/ou longitude no formato errado"
                        }
                    """),
                                            @ExampleObject(
                                                    name = "Latitude ausente",
                                                    value =
                                                            """
                        {
                            "message": "Required request parameter 'latidude' for method parameter type Double is present but converted to null"
                        }
                    """),
                                            @ExampleObject(
                                                    name = "Longitude ausente",
                                                    value =
                                                            """
                        {
                            "message": "Required request parameter 'longitude' for method parameter type Double is present but converted to null"
                        }
                    """),
                                            @ExampleObject(
                                                    name = "Range out of permited limit",
                                                    value =
                                                            """
                        {
                                "message": "raio de busca máxima é de 50.0 km"
                        }
                            """)
                                        })),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = DefaultResponse.class)))
            })
    @GetMapping("/recommendations")
    public ResponseEntity<NearbyPlacesResponse> getRecommendedPlaces(
            @RequestParam
                    @Parameter(description = "User's latitude (required)", required = true)
                    @Schema(example = "-29.35995", type = "Double")
                    Double latitude,
            @RequestParam
                    @Parameter(description = "User's longitude (required)", required = true)
                    @Schema(example = "-50.84805", type = "Double")
                    Double longitude,
            @RequestParam(required = false)
                    @Parameter(description = "Optional search radius in kilometers")
                    @Schema(example = "10", type = "Double")
                    Double range,
            @RequestParam(required = false, defaultValue = "5")
                    @Parameter(description = "Number of results per page")
                    @Schema(example = "5", type = "Integer")
                    Integer pageSize,
            @RequestParam(required = false, defaultValue = "")
                    @Parameter(description = "Token for pagination (opaque string)")
                    @Schema(
                            example = "MzoxNzMzNjg5MjQ1Njc4OmFCY0RlRmdIaUprTG1Obw:dGVzdGNoZWM",
                            type = "String")
                    String nextPageToken) {

        logger.info(
                "PLACE CONTROLLER - GET RECOMMENDED PLACES - lat: {} | lon: {} | pageSize: {} | nextPageToken: {}",
                latitude,
                longitude,
                pageSize,
                nextPageToken);

        var recommendedPlaces =
                placeRecommendationUseCase.getRecommendedPlaces(
                        latitude, longitude, range, pageSize, nextPageToken);

        if (recommendedPlaces.getPlaces().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, RECOMMENDATION_PLACE_NOT_FOUND_MESSAGE);
        }

        var placeResponses =
                recommendedPlaces.getPlaces().stream()
                        .map(
                                place ->
                                        PlaceResponse.fromDomain(
                                                place, Boolean.TRUE.equals(place.getIsSaved())))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(
                new NearbyPlacesResponse(placeResponses, recommendedPlaces.getNextTokenPage()));
    }
}
