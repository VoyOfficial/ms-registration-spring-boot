package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import br.voy.application.controller.request.PlaceRequest;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.entity.SponsorshipEligibility;
import br.voy.domain.exception.googlePlaces.PlacesApiClientException;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.ports.SponsorshipEligibilityPort;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import java.util.Map;
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
class PlaceRegistryIntegrationTest {

    private static final String REGISTRY_URL = "/api/registration/v1/places";
    private static final String PLACE_NAME = "Hard Rock Cafe Gramado";
    private static final String PLACE_CITY = "Gramado";
    private static final String GOOGLE_PLACE_ID = "ChIJ-registry-it-001";

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private SponsorshipEligibilityPort sponsorshipEligibilityPort;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean private GetNearbyPlacesService getNearbyPlacesService;

    @MockBean private GetPlaceDetailsService getPlaceDetailsService;

    @MockBean private GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @BeforeEach
    void setUp() {
        placeJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should register sponsored place and persist it in the database")
    void shouldRegisterSponsoredPlaceAndPersistItInTheDatabase() {
        stubEligible();
        when(googlePlacesPort.getPlaceFromText(eq(PLACE_NAME), eq(PLACE_CITY)))
                .thenReturn(googlePlaceDetails());

        ResponseEntity<Void> response =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).contains("/v1/places/");

        var saved = placeJpaRepository.findByGooglePlaceId(GOOGLE_PLACE_ID);
        assertThat(saved).isPresent();
        assertThat(saved.get().getName()).isEqualTo(PLACE_NAME);
        assertThat(saved.get().getCity()).isEqualTo(PLACE_CITY);
        assertThat(saved.get().getRanking()).isEqualTo(2);
        assertThat(saved.get().getStatus()).isTrue();
        assertThat(saved.get().getStartRecommendation()).isNotNull();
        assertThat(saved.get().getEndRecommendation()).isNotNull();
    }

    @Test
    @DisplayName("Should return 422 when sponsored place already exists by googlePlaceId")
    void shouldReturn422WhenSponsoredPlaceAlreadyExistsByGooglePlaceId() {
        stubEligible();
        when(googlePlacesPort.getPlaceFromText(eq(PLACE_NAME), eq(PLACE_CITY)))
                .thenReturn(googlePlaceDetails());

        ResponseEntity<Void> firstResponse =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Void.class);
        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Map> secondResponse =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Map.class);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(secondResponse.getBody()).isNotNull();
        assertThat(secondResponse.getBody().get("message").toString())
                .contains("already present in the database");
        assertThat(placeJpaRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Should return 403 when establishment subscription is inactive")
    void shouldReturn403WhenEstablishmentSubscriptionIsInactive() {
        when(sponsorshipEligibilityPort.resolveForRegistration(PLACE_NAME, PLACE_CITY))
                .thenReturn(
                        SponsorshipEligibility.builder()
                                .approved(true)
                                .subscriptionActive(false)
                                .build());

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Sponsorship Not Eligible");
        assertThat(placeJpaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return 403 when establishment is not approved")
    void shouldReturn403WhenEstablishmentIsNotApproved() {
        when(sponsorshipEligibilityPort.resolveForRegistration(PLACE_NAME, PLACE_CITY))
                .thenReturn(
                        SponsorshipEligibility.builder()
                                .approved(false)
                                .subscriptionActive(true)
                                .build());

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Sponsorship Not Eligible");
        assertThat(response.getBody().get("message").toString())
                .contains("not approved or does not have an active subscription");
        assertThat(placeJpaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return 400 when registry payload is incomplete")
    void shouldReturn400WhenRegistryPayloadIsIncomplete() {
        PlaceRequest request = PlaceRequest.builder().name(PLACE_NAME).build();

        ResponseEntity<Map> response =
                restTemplate.postForEntity(buildUrl(), jsonEntity(request), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(placeJpaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Should return 500 when Google Places integration fails")
    void shouldReturn500WhenGooglePlacesIntegrationFails() {
        stubEligible();
        when(googlePlacesPort.getPlaceFromText(anyString(), anyString()))
                .thenThrow(new PlacesApiClientException("error.places.api.default.message"));

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        buildUrl(), registryRequest(PLACE_NAME, PLACE_CITY), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(placeJpaRepository.findAll()).isEmpty();
    }

    private void stubEligible() {
        when(sponsorshipEligibilityPort.resolveForRegistration(PLACE_NAME, PLACE_CITY))
                .thenReturn(
                        SponsorshipEligibility.builder()
                                .approved(true)
                                .subscriptionActive(true)
                                .build());
    }

    private HttpEntity<PlaceRequest> registryRequest(String name, String city) {
        return jsonEntity(PlaceRequest.builder().name(name).city(city).ranking(2).build());
    }

    private HttpEntity<PlaceRequest> jsonEntity(PlaceRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }

    private PlaceDetails googlePlaceDetails() {
        return PlaceDetails.builder()
                .googlePlaceId(GOOGLE_PLACE_ID)
                .name(PLACE_NAME)
                .contact("(54) 3286-4040")
                .latitude(-29.378)
                .longitude(-50.873)
                .address("R. Borges de Medeiros, 3000 - Centro, Gramado - RS, 95670-000, Brazil")
                .build();
    }

    private String buildUrl() {
        return "http://localhost:" + port + REGISTRY_URL;
    }
}
