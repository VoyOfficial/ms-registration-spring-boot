package br.voy.infrastructure.agents;

import static org.assertj.core.api.Assertions.assertThat;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariables;

// Opt-in live Google calls. A present PLACES_API_KEY alone is not enough:
// Cloud Agent / CI secrets often lack Places billing and would fail the suite.
@EnabledIfEnvironmentVariables({
    @EnabledIfEnvironmentVariable(named = "RUN_PLACES_LIVE_IT", matches = "(?i)true|1|yes"),
    @EnabledIfEnvironmentVariable(named = "PLACES_API_KEY", matches = "(?!fake_key$).+")
})
class PlacesApiClientIT {

    private static final LatLng GRAMADO = new LatLng(-29.38542, -50.87790);

    private static final LatLng PIZZA_SEARCH = new LatLng(-29.366054, -50.877113);

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

    @Test
    @DisplayName("Should return pizza text search from Google Places API within 30 seconds")
    void shouldReturnPizzaTextSearchFromGooglePlacesApiWithinThirtySeconds() {
        long started = System.nanoTime();
        PlacesSearchResponse response =
                client.textSearchQuery("Pizza", PIZZA_SEARCH, 5000, PlaceType.RESTAURANT, null);
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);

        assertThat(elapsedMs).isLessThan(30_000);
        assertThat(response).isNotNull();
        assertThat(response.results).isNotEmpty();
        assertThat(response.results[0].placeId).isNotBlank();
        assertThat(response.results[0].name).isNotBlank();
    }

    @Test
    @DisplayName("Should download one list photo under the base64 ceiling")
    void shouldDownloadOneListPhotoUnderTheBase64Ceiling() {
        PlacesSearchResponse nearby =
                client.searchForNearbyPlaces(GRAMADO, 2000, PlaceType.RESTAURANT, null);
        PlacesSearchResult withPhoto =
                Arrays.stream(nearby.results)
                        .filter(result -> result.photos != null && result.photos.length > 0)
                        .findFirst()
                        .orElseThrow();

        Photo[] principalOnly = new Photo[] {withPhoto.photos[0]};
        long started = System.nanoTime();
        List<PlacePhoto> photos =
                client.getPlacePhotos(principalOnly, PlacesApiClient.LIST_PHOTO_MAX_WIDTH);
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started);

        assertThat(elapsedMs).isLessThan(15_000);
        assertThat(photos).hasSize(1);
        assertThat(photos.get(0).getImageBase64()).isNotBlank();
        assertThat(photos.get(0).getImageBase64().length())
                .isLessThanOrEqualTo(Place.MAX_LIST_PHOTO_BASE64_CHARS);
    }
}
