package br.voy.application.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.application.controller.request.PlaceRequest;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.model.SponsorModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.SponsorJpaRepository;
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
class PlaceRegistrySponsorEligibilityIntegrationTest {

    private static final String REGISTRY_URL = "/api/registration/v1/places";
    private static final String GOOGLE_PLACE_ID = "ChIJ-sponsor-it-001";

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @Autowired private SponsorJpaRepository sponsorJpaRepository;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean private GetNearbyPlacesService getNearbyPlacesService;

    @MockBean private GetPlaceDetailsService getPlaceDetailsService;

    @MockBean private GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @BeforeEach
    void setUp() {
        placeJpaRepository.deleteAll();
        sponsorJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return 201 when the sponsor table has an approved active match")
    void shouldReturn201WhenTheSponsorTableHasAnApprovedActiveMatch() {
        saveSponsor("Hard Rock Cafe Gramado", "gramado", true, true);
        when(googlePlacesPort.getPlaceFromText(anyString(), anyString()))
                .thenReturn(googlePlaceDetails("hard rock cafe gramado"));

        ResponseEntity<Void> response =
                restTemplate.postForEntity(
                        buildUrl(),
                        registryRequest("  hard   rock cafe gramado  ", "Gramado"),
                        Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(placeJpaRepository.findByGooglePlaceId(GOOGLE_PLACE_ID)).isPresent();
    }

    @Test
    @DisplayName("Should return 403 when the sponsor is missing, unapproved, or inactive")
    void shouldReturn403WhenTheSponsorIsMissingUnapprovedOrInactive() {
        saveSponsor("Cafe Colonial", "Gramado", false, true);
        saveSponsor("Restaurante X", "Gramado", true, false);

        assertForbidden("Unknown Place", "Gramado");
        assertForbidden("Cafe Colonial", "Gramado");
        assertForbidden("Restaurante X", "Gramado");

        verify(googlePlacesPort, never()).getPlaceFromText(anyString(), anyString());
        assertThat(placeJpaRepository.findAll()).isEmpty();
    }

    private void assertForbidden(String name, String city) {
        ResponseEntity<Map> response =
                restTemplate.postForEntity(buildUrl(), registryRequest(name, city), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Sponsorship Not Eligible");
    }

    private void saveSponsor(
            String name, String city, boolean approved, boolean subscriptionActive) {
        sponsorJpaRepository.save(
                SponsorModel.builder()
                        .name(name)
                        .city(city)
                        .approved(approved)
                        .subscriptionActive(subscriptionActive)
                        .build());
    }

    private HttpEntity<PlaceRequest> registryRequest(String name, String city) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(
                PlaceRequest.builder().name(name).city(city).ranking(2).build(), headers);
    }

    private PlaceDetails googlePlaceDetails(String name) {
        return PlaceDetails.builder()
                .googlePlaceId(GOOGLE_PLACE_ID)
                .name(name)
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
