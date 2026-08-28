package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.application.controller.response.NearbyPlacesResponse;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.model.PlaceModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaceNearbyPersistenceIntegrationTest {

    private static final String NEARBY_PLACES_URL = "/api/registration/v1/places";
    private static final String BASE_URL_TEMPLATE =
            "http://localhost:%d%s?latitude=-29.38&longitude=-50.87";

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean private GetPlaceDetailsService getPlaceDetailsService;

    @MockBean private PlaceRegistryService placeRegistryService;

    @MockBean private GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @BeforeEach
    void setUp() {
        placeJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should persist places returned by Google API to the database")
    void shouldPersistPlacesReturnedByGoogleApiToDatabase() {
        var place1 =
                Place.builder()
                        .googlePlaceId("persist-test-id-001")
                        .name("Restaurant Alpha")
                        .city("Test City")
                        .latitude(-29.385)
                        .longitude(-50.877)
                        .build();

        var place2 =
                Place.builder()
                        .googlePlaceId("persist-test-id-002")
                        .name("Restaurant Beta")
                        .city("Test City")
                        .latitude(-29.386)
                        .longitude(-50.878)
                        .build();

        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(place1, place2), null));

        restTemplate.getForEntity(buildUrl(), String.class);

        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(
                        () -> {
                            assertThat(
                                            placeJpaRepository.findByGooglePlaceId(
                                                    "persist-test-id-001"))
                                    .isPresent();
                            assertThat(
                                            placeJpaRepository.findByGooglePlaceId(
                                                    "persist-test-id-002"))
                                    .isPresent();
                        });

        assertThat(placeJpaRepository.findByGooglePlaceId("persist-test-id-001").get().getName())
                .isEqualTo("Restaurant Alpha");
        assertThat(placeJpaRepository.findByGooglePlaceId("persist-test-id-002").get().getName())
                .isEqualTo("Restaurant Beta");
        assertThat(placeJpaRepository.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("Should not create duplicate entry when same Google place is received again")
    void shouldNotCreateDuplicateEntryWhenSameGooglePlaceIsReceivedAgain() {
        var place =
                Place.builder()
                        .googlePlaceId("persist-test-dup-001")
                        .name("Restaurant Gamma")
                        .city("Test City")
                        .latitude(0.0)
                        .longitude(0.0)
                        .build();

        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(place), null));

        restTemplate.getForEntity(buildUrl(), String.class);

        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(500))
                .until(
                        () ->
                                placeJpaRepository
                                        .findByGooglePlaceId("persist-test-dup-001")
                                        .isPresent());

        restTemplate.getForEntity(buildUrl(), String.class);

        await().during(2, TimeUnit.SECONDS)
                .atMost(4, TimeUnit.SECONDS)
                .until(
                        () ->
                                placeJpaRepository.findAll().stream()
                                                .filter(
                                                        p ->
                                                                "persist-test-dup-001"
                                                                        .equals(
                                                                                p
                                                                                        .getGooglePlaceId()))
                                                .count()
                                        == 1L);
    }

    @Test
    @DisplayName("When places exist in database, should return them without calling Google API")
    void shouldReturnPlacesFromDatabaseWithoutCallingGoogleApi() {
        placeJpaRepository.saveAll(
                List.of(
                        PlaceModel.builder()
                                .googlePlaceId("db-src-001")
                                .name("Place One")
                                .city("Test City")
                                .latitude(-29.382)
                                .longitude(-50.872)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-src-002")
                                .name("Place Two")
                                .city("Test City")
                                .latitude(-29.383)
                                .longitude(-50.873)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-src-003")
                                .name("Place Three")
                                .city("Test City")
                                .latitude(-29.384)
                                .longitude(-50.874)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-src-004")
                                .name("Place Four")
                                .city("Test City")
                                .latitude(-29.385)
                                .longitude(-50.875)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-src-005")
                                .name("Place Five")
                                .city("Test City")
                                .latitude(-29.386)
                                .longitude(-50.876)
                                .build()));

        var response = restTemplate.getForEntity(buildUrl(), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPlaces()).isNotEmpty();
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), any(), any());
    }

    @Test
    @DisplayName("When nextToken exhausts database places, should fall back to Google API")
    void shouldFallBackToGoogleApiWhenDatabasePlacesAreExhaustedByNextToken() {
        placeJpaRepository.saveAll(
                List.of(
                        PlaceModel.builder()
                                .googlePlaceId("db-page-001")
                                .name("Place One")
                                .city("Test City")
                                .latitude(-29.382)
                                .longitude(-50.872)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-page-002")
                                .name("Place Two")
                                .city("Test City")
                                .latitude(-29.383)
                                .longitude(-50.873)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-page-003")
                                .name("Place Three")
                                .city("Test City")
                                .latitude(-29.384)
                                .longitude(-50.874)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-page-004")
                                .name("Place Four")
                                .city("Test City")
                                .latitude(-29.385)
                                .longitude(-50.875)
                                .build(),
                        PlaceModel.builder()
                                .googlePlaceId("db-page-005")
                                .name("Place Five")
                                .city("Test City")
                                .latitude(-29.386)
                                .longitude(-50.876)
                                .build()));

        var googlePlace =
                Place.builder()
                        .googlePlaceId("google-fallback-001")
                        .name("Google Place")
                        .city("Test City")
                        .latitude(-29.390)
                        .longitude(-50.880)
                        .build();
        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(googlePlace), null));

        var firstResponse = restTemplate.getForEntity(buildUrl(), NearbyPlacesResponse.class);
        assertThat(firstResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(firstResponse.getBody()).isNotNull();

        String nextToken = firstResponse.getBody().getNextTokenPage();
        assertThat(nextToken).isNotNull();

        var secondResponse =
                restTemplate.getForEntity(
                        buildUrl() + "&nextPageToken=" + nextToken, NearbyPlacesResponse.class);
        assertThat(secondResponse.getStatusCode().value()).isEqualTo(200);

        verify(googlePlacesPort).getNearbyPlaces(any(), any(), any(), any());
    }

    private String buildUrl() {
        return String.format(BASE_URL_TEMPLATE, port, NEARBY_PLACES_URL);
    }
}
