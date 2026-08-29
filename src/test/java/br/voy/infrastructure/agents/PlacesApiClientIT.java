package br.voy.infrastructure.agents;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "PLACES_API_KEY", matches = "(?!fake_key$).+")
class PlacesApiClientIT {

    private static final LatLng GRAMADO = new LatLng(-29.38542, -50.87790);

    private PlacesApiClient client;

    @BeforeEach
    void setUp() {
        client = new PlacesApiClient(System.getenv("PLACES_API_KEY"));
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Test
    @DisplayName("Should return nearby places from Google Places API")
    void shouldReturnNearbyPlacesFromGooglePlacesApi() {
        PlacesSearchResponse response =
                client.searchForNearbyPlaces(GRAMADO, 2000, PlaceType.RESTAURANT, null);

        assertThat(response).isNotNull();
        assertThat(response.results).isNotEmpty();
        assertThat(response.results[0].placeId).isNotBlank();
        assertThat(response.results[0].name).isNotBlank();
    }

    @Test
    @DisplayName("Should return place details from Google Places API")
    void shouldReturnPlaceDetailsFromGooglePlacesApi() {
        PlacesSearchResponse nearby =
                client.searchForNearbyPlaces(GRAMADO, 2000, PlaceType.RESTAURANT, null);
        String placeId = nearby.results[0].placeId;

        PlaceDetails details = client.getPlaceDetails(placeId);

        assertThat(details).isNotNull();
        assertThat(details.placeId).isEqualTo(placeId);
        assertThat(details.name).isNotBlank();
    }
}
