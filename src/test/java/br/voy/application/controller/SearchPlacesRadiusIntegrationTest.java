package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchPlacesRadiusIntegrationTest {

    private static final double MOUNTAIN_VIEW_LATITUDE = 37.421998333333335;
    private static final double MOUNTAIN_VIEW_LONGITUDE = -122.084;
    private static final double GRAMADO_LATITUDE = -29.366054;
    private static final double GRAMADO_LONGITUDE = -50.877113;
    private static final String SEARCH_BODY = "{\"types\":[],\"ordination\":\"Mais avaliados\"}";

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private PlaceRepository placeRepository;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @Autowired private PlacePhotoJpaRepository placePhotoJpaRepository;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private PlacesApiClient placesApiClient;

    @BeforeEach
    void cleanDatabase() {
        placePhotoJpaRepository.deleteAll();
        placeJpaRepository.deleteAll();
        when(googlePlacesPort.textSearch(any(), any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));
    }

    @Test
    @DisplayName("Mountain View search keeps a nearby pizza and drops Gramado")
    void mountainViewSearchDropsPlacesOutsideFiveKilometers() throws Exception {
        save("near-pizza", "Pizza Guys", 4.2f, 37.4220541, -122.0853242);
        save("outside-pizza", "Pizza Farther", 4.9f, 37.47245, -122.084);
        save("gramado-pizza", "Pizzaria Dom Formagio", 5.0f, -29.3705632, -50.8786955);

        assertOnly(placesFrom(MOUNTAIN_VIEW_LATITUDE, MOUNTAIN_VIEW_LONGITUDE), "Pizza Guys");
    }

    @Test
    @DisplayName("Gramado search keeps the local pizza and drops Mountain View")
    void gramadoSearchDropsMountainViewPizza() throws Exception {
        save("near-pizza", "Pizza Guys", 4.9f, 37.4220541, -122.0853242);
        save("gramado-pizza", "Pizzaria Dom Formagio", 4.2f, -29.3705632, -50.8786955);

        assertOnly(placesFrom(GRAMADO_LATITUDE, GRAMADO_LONGITUDE), "Pizzaria Dom Formagio");
    }

    @Test
    @DisplayName("A full page of distant rows still asks Google and drops remote hits")
    void distantCatalogCallsGoogleAndDropsRemoteHits() throws Exception {
        for (int index = 0; index < 20; index++) {
            save("gramado-" + index, "Pizza Gramado " + index, 5.0f, -29.3705632, -50.8786955);
        }
        when(googlePlacesPort.textSearch(any(), any(), any(), any(), any()))
                .thenReturn(
                        new NearbyPlaces(
                                List.of(
                                        place(
                                                "google-near",
                                                "Pizza Google",
                                                4.8f,
                                                37.423,
                                                -122.086),
                                        place(
                                                "google-far",
                                                "Pizza Remote",
                                                4.9f,
                                                -29.3705632,
                                                -50.8786955)),
                                null));

        assertOnly(placesFrom(MOUNTAIN_VIEW_LATITUDE, MOUNTAIN_VIEW_LONGITUDE), "Pizza Google");
        verify(googlePlacesPort)
                .textSearch(eq("Pizza"), any(Coordinates.class), eq(5000), isNull(), isNull());
        assertThat(placeJpaRepository.findByGooglePlaceId("google-near")).isPresent();
        assertThat(placeJpaRepository.findByGooglePlaceId("google-far")).isEmpty();
    }

    private void save(
            String googlePlaceId, String name, float rating, double latitude, double longitude) {
        placeRepository.savePlace(place(googlePlaceId, name, rating, latitude, longitude));
    }

    private static Place place(
            String googlePlaceId, String name, float rating, double latitude, double longitude) {
        return Place.builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .address(name)
                .rating(rating)
                .userRatingsTotal(10)
                .latitude(latitude)
                .longitude(longitude)
                .googleTypes("restaurant")
                .photos(new ArrayList<>())
                .build();
    }

    private JsonNode placesFrom(double latitude, double longitude) throws Exception {
        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        searchUrl(latitude, longitude), json(SEARCH_BODY), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return objectMapper.readTree(response.getBody()).path("places");
    }

    private static void assertOnly(JsonNode places, String name) {
        assertThat(names(places)).containsExactly(name);
        assertThat(distanceKm(places.get(0))).isLessThan(5.0);
    }

    private String searchUrl(double latitude, double longitude) {
        return "http://localhost:"
                + port
                + "/api/registration/v1/places/search?place=Pizza&latitude="
                + latitude
                + "&longitude="
                + longitude;
    }

    private static HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private static List<String> names(JsonNode places) {
        return StreamSupport.stream(places.spliterator(), false)
                .map(place -> place.path("name").asText())
                .collect(Collectors.toList());
    }

    private static double distanceKm(JsonNode place) {
        return Double.parseDouble(
                place.path("distanceFromUserLocation").asText().replace(" km", ""));
    }
}
