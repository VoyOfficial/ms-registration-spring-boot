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
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.model.PlaceModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Base64;
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

    @Autowired private PlacePhotoJpaRepository placePhotoJpaRepository;

    @Autowired private PlaceRepository placeRepository;

    @BeforeEach
    void setUp() {
        placePhotoJpaRepository.deleteAll();
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
        assertThat(
                        placeJpaRepository.findAll().stream()
                                .map(PlaceModel::getGooglePlaceId)
                                .filter(id -> id.startsWith("persist-test-id-"))
                                .count())
                .isEqualTo(2);
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
        placeJpaRepository.saveAll(seedNearbyPlaces("db-src", 20));

        var response = restTemplate.getForEntity(buildUrl(), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPlaces()).isNotEmpty();
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), any(), any());
    }

    @Test
    @DisplayName("When nextToken exhausts database places, should fall back to Google API")
    void shouldFallBackToGoogleApiWhenDatabasePlacesAreExhaustedByNextToken() {
        placeJpaRepository.saveAll(seedNearbyPlaces("db-page", 20));

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

    @Test
    @DisplayName("Should return principal photo base64 for a stored place and a Google place")
    void shouldReturnPrincipalPhotoBase64ForStoredAndGooglePlaces() throws Exception {
        byte[] storedImage = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 1, 2, 3, 4};
        byte[] otherImage = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 9, 8, 7};
        String storedBase64 = Base64.getEncoder().encodeToString(storedImage);
        String otherBase64 = Base64.getEncoder().encodeToString(otherImage);
        String googleBase64 = "R29vZ2xlQmFubmVy";

        placeRepository.savePlace(
                Place.builder()
                        .googlePlaceId("banner-db-001")
                        .name("Persisted Banner")
                        .address("Rua 1")
                        .city("Canela")
                        .latitude(-29.381)
                        .longitude(-50.871)
                        .principalPhoto("principal-ref")
                        .principalPhotoUrl(
                                "https://maps.googleapis.com/maps/api/place/photo?photo_reference=principal-ref&key=secret-db-key")
                        .googleTypes("tourist_attraction")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("other-ref")
                                                .imageBase64(otherBase64)
                                                .photoUrl(
                                                        "https://example.com/other?key=secret-db-key")
                                                .build(),
                                        PlacePhoto.builder()
                                                .photoReference("principal-ref")
                                                .imageBase64(storedBase64)
                                                .photoUrl(
                                                        "https://example.com/principal?key=secret-db-key")
                                                .build()))
                        .build());

        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(
                        new NearbyPlaces(
                                List.of(
                                        Place.builder()
                                                .googlePlaceId("banner-google-001")
                                                .name("Google Banner")
                                                .latitude(-29.382)
                                                .longitude(-50.872)
                                                .principalPhoto("google-ref")
                                                .principalPhotoUrl(
                                                        "https://maps.googleapis.com/maps/api/place/photo?photo_reference=google-ref&key=secret-google-key")
                                                .photos(
                                                        List.of(
                                                                PlacePhoto.builder()
                                                                        .photoReference(
                                                                                "google-ref")
                                                                        .imageBase64(googleBase64)
                                                                        .build()))
                                                .build()),
                                null));

        var response = restTemplate.getForEntity(buildUrl(), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        JsonNode places = new ObjectMapper().readTree(response.getBody()).path("places");
        assertThat(photoNamed(places, "Persisted Banner")).isEqualTo(storedBase64);
        assertThat(photoNamed(places, "Google Banner")).isEqualTo(googleBase64);
        assertThat(places.findValues("photos")).isEmpty();
        assertThat(response.getBody()).doesNotContain("secret-db-key");
        assertThat(response.getBody()).doesNotContain("secret-google-key");
        assertThat(response.getBody()).doesNotContain(otherBase64);
    }

    @Test
    @DisplayName("Should return a stored photo that sits on the list size ceiling")
    void shouldReturnStoredPhotoAtTheListSizeCeiling() throws Exception {
        String banner = rawImageBase64(150_000);
        saveBannerPlace("limit-photo-001", "Limit Banner", "limit-ref", banner);
        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var response = restTemplate.getForEntity(buildUrl(), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        JsonNode places = new ObjectMapper().readTree(response.getBody()).path("places");
        assertThat(photoNamed(places, "Limit Banner")).isEqualTo(banner);
    }

    @Test
    @DisplayName("Should omit an oversized stored photo and still return the place")
    void shouldOmitOversizedStoredPhotoWithoutFailingTheList() throws Exception {
        String banner = rawImageBase64(150_001);
        saveBannerPlace("huge-photo-001", "Huge Banner", "huge-ref", banner);
        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var response = restTemplate.getForEntity(buildUrl(), String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).doesNotContain(banner);
        JsonNode places = new ObjectMapper().readTree(response.getBody()).path("places");
        assertThat(places).hasSize(1);
        assertThat(photoNamed(places, "Huge Banner")).isNull();
    }

    private void saveBannerPlace(
            String googlePlaceId, String name, String reference, String banner) {
        placeRepository.savePlace(
                Place.builder()
                        .googlePlaceId(googlePlaceId)
                        .name(name)
                        .address("Rua 1")
                        .city("Canela")
                        .latitude(-29.381)
                        .longitude(-50.871)
                        .principalPhoto(reference)
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference(reference)
                                                .imageBase64(banner)
                                                .build()))
                        .build());
    }

    private static String rawImageBase64(int byteCount) {
        byte[] raw = new byte[byteCount];
        raw[0] = (byte) 0x89;
        if (byteCount > 1) {
            raw[1] = 0x50;
        }
        return Base64.getEncoder().encodeToString(raw);
    }

    private static String photoNamed(JsonNode places, String name) {
        for (JsonNode place : places) {
            if (name.equals(place.path("name").asText())) {
                JsonNode photo = place.path("photo");
                return photo.isMissingNode() || photo.isNull() ? null : photo.asText();
            }
        }
        return null;
    }

    private List<PlaceModel> seedNearbyPlaces(String idPrefix, int count) {
        List<PlaceModel> places = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            places.add(
                    PlaceModel.builder()
                            .googlePlaceId(idPrefix + "-" + String.format("%03d", i))
                            .name("Place " + i)
                            .address("Test Address")
                            .city("Test City")
                            .latitude(-29.382 - (i * 0.001))
                            .longitude(-50.872 - (i * 0.001))
                            .build());
        }
        return places;
    }

    private String buildUrl() {
        return String.format(BASE_URL_TEMPLATE, port, NEARBY_PLACES_URL);
    }
}
