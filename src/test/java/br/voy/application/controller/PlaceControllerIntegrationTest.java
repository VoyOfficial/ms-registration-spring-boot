package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaceControllerIntegrationTest {

    private static final String CONTEXT_PATH = "/api/registration";
    private static final String NEARBY_PLACES_URL = CONTEXT_PATH + "/v1/places";
    private static final String RECOMMENDATIONS_URL = CONTEXT_PATH + "/v1/places/recommendations";

    private static final Double NEARBY_LATITUDE = -29.385420483712636;
    private static final Double NEARBY_LONGITUDE = -50.877900418774004;
    private static final Double RECOMMENDATIONS_LATITUDE = -29.385436;
    private static final Double RECOMMENDATIONS_LONGITUDE = -50.877608;

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @MockBean private PlaceRepository placeRepository;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean GetNearbyPlacesService getNearbyPlacesService;

    @MockBean GetPlaceDetailsService getPlaceDetailsService;

    @MockBean PlaceRegistryService placeRegistryService;

    @MockBean GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @Test
    @DisplayName("Should return 200 with nearby places for valid coordinates")
    void shouldReturn200WithNearbyPlacesForValidCoordinates() {

        // scenario
        var nearbyPlaces = createNearbyPlaces(5);

        doReturn(nearbyPlaces)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        var url =
                buildUrl(
                        NEARBY_PLACES_URL,
                        "latitude=" + NEARBY_LATITUDE + "&longitude=" + NEARBY_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("places");
    }

    @Test
    @DisplayName("Should return 200 with 20 nearby places for valid coordinates")
    void shouldReturn200With20NearbyPlacesForValidCoordinates() {

        // scenario
        var nearbyPlaces = createNearbyPlaces(20);

        doReturn(nearbyPlaces)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        var url =
                buildUrl(
                        NEARBY_PLACES_URL,
                        "latitude=" + NEARBY_LATITUDE + "&longitude=" + NEARBY_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Place0");
        assertThat(response.getBody()).contains("Place19");
    }

    @Test
    @DisplayName("Should return 200 with nextTokenPage when more nearby places exist")
    void shouldReturn200WithNextTokenPageWhenMoreNearbyPlacesExist() {

        // scenario
        var nextTokenPage = "AZose0kJX6a_nextToken";
        var nearbyPlaces = new NearbyPlaces(createPlaceList(5), nextTokenPage);

        doReturn(nearbyPlaces)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        var url =
                buildUrl(
                        NEARBY_PLACES_URL,
                        "latitude=" + NEARBY_LATITUDE + "&longitude=" + NEARBY_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(nextTokenPage);
    }

    @Test
    @DisplayName("Should return 200 with nearby places using custom radius and placeType")
    void shouldReturn200WithNearbyPlacesUsingCustomRadiusAndPlaceType() {

        // scenario
        var nearbyPlaces = createNearbyPlaces(3);

        doReturn(nearbyPlaces)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        var url =
                buildUrl(
                        NEARBY_PLACES_URL,
                        "latitude="
                                + NEARBY_LATITUDE
                                + "&longitude="
                                + NEARBY_LONGITUDE
                                + "&radius=3000&placeType=restaurant");

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("places");
    }

    @Test
    @DisplayName("Should return 200 with recommended places for valid coordinates")
    void shouldReturn200WithRecommendedPlacesForValidCoordinates() {

        // scenario
        var recommendedPlaces = createRecommendedPlacesResponse(5, false);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(anyDouble(), anyDouble(), any(), anyInt(), anyString());

        // action - validation
        var url =
                buildUrl(
                        RECOMMENDATIONS_URL,
                        "latitude="
                                + RECOMMENDATIONS_LATITUDE
                                + "&longitude="
                                + RECOMMENDATIONS_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("places");
    }

    @Test
    @DisplayName("Should return 200 with recommendations and nextTokenPage when more pages exist")
    void shouldReturn200WithRecommendationsAndNextTokenPageWhenMorePagesExist() {

        // scenario
        var nextToken = "next-page-token-123";
        var recommendedPlaces = createRecommendedPlacesResponse(5, true);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(anyDouble(), anyDouble(), any(), anyInt(), anyString());

        // action - validation
        var url =
                buildUrl(
                        RECOMMENDATIONS_URL,
                        "latitude="
                                + RECOMMENDATIONS_LATITUDE
                                + "&longitude="
                                + RECOMMENDATIONS_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(nextToken);
    }

    @Test
    @DisplayName("Should return 404 when no recommended places found for coordinates")
    void shouldReturn404WhenNoRecommendedPlacesFoundForCoordinates() {

        // scenario
        var emptyResponse = new NearbyPlaces(new ArrayList<>(), null);

        doReturn(emptyResponse)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(anyDouble(), anyDouble(), any(), anyInt(), anyString());

        // action - validation
        var url =
                buildUrl(
                        RECOMMENDATIONS_URL,
                        "latitude="
                                + RECOMMENDATIONS_LATITUDE
                                + "&longitude="
                                + RECOMMENDATIONS_LONGITUDE);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 200 with recommendations using custom pageSize")
    void shouldReturn200WithRecommendationsUsingCustomPageSize() {

        // scenario
        var pageSize = 10;
        var recommendedPlaces = createRecommendedPlacesResponse(pageSize, false);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(anyDouble(), anyDouble(), any(), anyInt(), anyString());

        // action - validation
        var url =
                buildUrl(
                        RECOMMENDATIONS_URL,
                        "latitude="
                                + RECOMMENDATIONS_LATITUDE
                                + "&longitude="
                                + RECOMMENDATIONS_LONGITUDE
                                + "&pageSize="
                                + pageSize);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Place9");
    }

    @Test
    @DisplayName("Should return 200 navigating to next page with nextPageToken for recommendations")
    void shouldReturn200NavigatingToNextPageWithNextPageTokenForRecommendations() {

        // scenario
        var nextPageToken = "page-token-from-previous-request";
        var recommendedPlaces = createRecommendedPlacesResponse(5, false);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(anyDouble(), anyDouble(), any(), anyInt(), anyString());

        // action - validation
        var url =
                buildUrl(
                        RECOMMENDATIONS_URL,
                        "latitude="
                                + RECOMMENDATIONS_LATITUDE
                                + "&longitude="
                                + RECOMMENDATIONS_LONGITUDE
                                + "&nextPageToken="
                                + nextPageToken);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("places");
    }

    private String buildUrl(String path, String queryParams) {
        return "http://localhost:" + port + path + "?" + queryParams;
    }

    private NearbyPlaces createNearbyPlaces(int size) {
        return new NearbyPlaces(createPlaceList(size), null);
    }

    private List<Place> createPlaceList(int size) {
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            places.add(createPlace("ChIJq6qq6oZJGZURlUgeg2eJ3b" + i, i));
        }
        return places;
    }

    private NearbyPlaces createRecommendedPlacesResponse(int size, boolean hasNextPage) {
        String nextToken = hasNextPage ? "next-page-token-123" : null;
        return new NearbyPlaces(createPlaceList(size), nextToken);
    }

    private static Place createPlace(String id, Integer index) {
        return new Place(
                null,
                id,
                "Place" + index,
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.",
                "(54) 3286-1362",
                null,
                4.7f,
                2599,
                false, // isSaved
                "photoReference",
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=photoReference&key=test_key",
                List.of(new PlacePhoto(), new PlacePhoto()),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil",
                "Gramado",
                "RS",
                true,
                1,
                null,
                null,
                null,
                null,
                null,
                65.2f,
                NEARBY_LATITUDE,
                NEARBY_LONGITUDE,
                "");
    }
}
