package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.voy.application.controller.response.NearbyPlacesResponse;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.utils.PaginationTokenEncoder;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.testsupport.RecommendedPlacesTestData;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
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
class RecommendedPlacesIntegrationTest {

    private static final String RECOMMENDATIONS_URL = "/api/registration/v1/places/recommendations";

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private GetNearbyPlacesService getNearbyPlacesService;

    @MockBean private GetPlaceDetailsService getPlaceDetailsService;

    @MockBean private PlaceRegistryService placeRegistryService;

    @BeforeEach
    void setUp() {
        RecommendedPlacesTestData.seed(placeJpaRepository);
    }

    @Test
    @DisplayName("Should return first page of active recommendations from database seed")
    void shouldReturnFirstPageOfActiveRecommendationsFromDatabaseSeed() {
        ResponseEntity<NearbyPlacesResponse> response =
                restTemplate.getForEntity(buildUrl("pageSize=5"), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPlaces()).hasSize(5);
        assertThat(response.getBody().getNextTokenPage()).isNotBlank();
        assertThat(collectNames(response.getBody())).doesNotContain("Hard Rock Cafe");
    }

    @Test
    @DisplayName("Should paginate through all active recommendations from issue #31 seed")
    void shouldPaginateThroughAllActiveRecommendationsFromIssue31Seed() {
        Set<String> allNames = new HashSet<>();
        String nextToken = null;
        int pageCount = 0;

        do {
            String url =
                    nextToken == null ? buildUrl("pageSize=5") : buildUrl("pageSize=5", nextToken);
            ResponseEntity<NearbyPlacesResponse> response =
                    restTemplate.getForEntity(url, NearbyPlacesResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getPlaces()).isNotEmpty();

            allNames.addAll(collectNames(response.getBody()));
            nextToken = response.getBody().getNextTokenPage();
            pageCount++;
        } while (nextToken != null && pageCount < 10);

        assertThat(allNames).hasSize(11);
        assertThat(allNames).doesNotContain("Hard Rock Cafe");
        assertThat(allNames)
                .contains(
                        "Fonte do Amor Eterno",
                        "Mini Mundo",
                        "Lago Negro",
                        "Space Adventure Canela");
    }

    @Test
    @DisplayName("Should return 200 with empty places when pagination is past the last page")
    void shouldReturn200WithEmptyPlacesWhenPaginationIsPastLastPage() {
        String pastEndToken = PaginationTokenEncoder.encode(10_000);

        ResponseEntity<NearbyPlacesResponse> pastEndResponse =
                restTemplate.getForEntity(
                        buildUrl("pageSize=5", pastEndToken), NearbyPlacesResponse.class);

        assertThat(pastEndResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pastEndResponse.getBody()).isNotNull();
        assertThat(pastEndResponse.getBody().getPlaces()).isEmpty();
        assertThat(pastEndResponse.getBody().getNextTokenPage()).isNull();
    }

    @Test
    @DisplayName("Should exclude expired Hard Rock Cafe from recommendations")
    void shouldExcludeExpiredHardRockCafeFromRecommendations() {
        ResponseEntity<NearbyPlacesResponse> response =
                restTemplate.getForEntity(buildUrl("pageSize=50"), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(collectNames(response.getBody())).doesNotContain("Hard Rock Cafe");
    }

    @Test
    @DisplayName("Should return recommendations sorted by ranking then distance")
    void shouldReturnRecommendationsSortedByRankingThenDistance() {
        ResponseEntity<NearbyPlacesResponse> response =
                restTemplate.getForEntity(buildUrl("pageSize=50"), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var places = response.getBody().getPlaces();
        assertThat(places.get(0).getRanking()).isLessThanOrEqualTo(places.get(1).getRanking());
    }

    @Test
    @DisplayName("Should include distance from user location in recommendations")
    void shouldIncludeDistanceFromUserLocationInRecommendations() {
        ResponseEntity<NearbyPlacesResponse> response =
                restTemplate.getForEntity(buildUrl("pageSize=5"), NearbyPlacesResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getPlaces())
                .allMatch(
                        place ->
                                place.getDistanceFromUserLocation() != null
                                        && !place.getDistanceFromUserLocation().isBlank());
    }

    @Test
    @DisplayName("Should return 404 when no recommended places exist for coordinates")
    void shouldReturn404WhenNoRecommendedPlacesExistForCoordinates() {
        placeJpaRepository.deleteAll();

        ResponseEntity<String> response =
                restTemplate.getForEntity(
                        buildUrlForCoordinates(0.0, 0.0, "pageSize=5"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("não encontrado");
    }

    @Test
    @DisplayName("Should return 400 when search range exceeds maximum")
    void shouldReturn400WhenSearchRangeExceedsMaximum() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(buildUrl("pageSize=5&range=100"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("raio de busca máxima é de 50.0 km");
    }

    @Test
    @DisplayName("Should return 400 when page size exceeds maximum")
    void shouldReturn400WhenPageSizeExceedsMaximum() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(buildUrl("pageSize=51"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private String buildUrl(String extraParams) {
        return buildUrlForCoordinates(
                RecommendedPlacesTestData.GRAMADO_LATITUDE,
                RecommendedPlacesTestData.GRAMADO_LONGITUDE,
                extraParams);
    }

    private String buildUrl(String extraParams, String nextPageToken) {
        return buildUrlForCoordinates(
                RecommendedPlacesTestData.GRAMADO_LATITUDE,
                RecommendedPlacesTestData.GRAMADO_LONGITUDE,
                extraParams + "&nextPageToken=" + nextPageToken);
    }

    private String buildUrlForCoordinates(double latitude, double longitude, String extraParams) {
        return "http://localhost:"
                + port
                + RECOMMENDATIONS_URL
                + "?latitude="
                + latitude
                + "&longitude="
                + longitude
                + "&"
                + extraParams;
    }

    private static Set<String> collectNames(NearbyPlacesResponse response) {
        return response.getPlaces().stream().map(p -> p.getName()).collect(Collectors.toSet());
    }
}
