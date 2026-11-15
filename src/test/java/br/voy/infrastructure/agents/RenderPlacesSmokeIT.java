package br.voy.infrastructure.agents;

import static org.assertj.core.api.Assertions.assertThat;

import br.voy.domain.entity.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

/**
 * Live HTTP checks against a running registration service that talks to real Google Places (mock
 * off). Opt-in because every run hits Google and search may persist places.
 *
 * <p>Local API (recommended before merge when Places Google paths change):
 *
 * <pre>
 * ENABLE_MOCK_SERVICES=false PLACES_API_KEY=... java -jar target/*.jar
 * RUN_PLACES_SMOKE_IT=true PLACES_SMOKE_BASE_URL=http://localhost:8080 \
 *   ./mvnw -Dtest=RenderPlacesSmokeIT test
 * </pre>
 *
 * <p>Deployed Render: {@code RUN_RENDER_IT=true} (or {@code RUN_PLACES_SMOKE_IT=true} with the
 * default base URL).
 */
@EnabledIf("isEnabled")
class RenderPlacesSmokeIT {

    private static final String DEFAULT_BASE_URL = "https://ms-registration.onrender.com";

    private static final String BASE_URL =
            System.getenv().getOrDefault("PLACES_SMOKE_BASE_URL", DEFAULT_BASE_URL);

    private static final long MAX_ELAPSED_MS = 30_000;

    private final HttpClient httpClient =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    static boolean isEnabled() {
        return isTruthy(System.getenv("RUN_PLACES_SMOKE_IT"))
                || isTruthy(System.getenv("RUN_RENDER_IT"));
    }

    private static boolean isTruthy(String value) {
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("true") || value.equals("1") || value.equalsIgnoreCase("yes");
    }

    @Test
    @DisplayName("Nearby list returns within 30 seconds")
    void nearbyListReturnsWithinThirtySeconds() throws Exception {
        JsonNode body = get("/api/registration/v1/places?latitude=-29.385436&longitude=-50.877608");

        assertThat(body.path("places").isArray()).isTrue();
        assertThat(body.path("places").size()).isGreaterThan(0);
        assertPhotosStaySmall(body.path("places"));
    }

    @Test
    @DisplayName("Recommendations return within 30 seconds")
    void recommendationsReturnWithinThirtySeconds() throws Exception {
        JsonNode body =
                get(
                        "/api/registration/v1/places/recommendations?latitude=-29.385436&longitude=-50.877608&pageSize=5");

        assertThat(body.path("places").isArray()).isTrue();
        assertThat(body.path("places").size()).isGreaterThan(0);
        assertPhotosStaySmall(body.path("places"));
    }

    @Test
    @DisplayName("Pizza search returns within 30 seconds")
    void pizzaSearchReturnsWithinThirtySeconds() throws Exception {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                                + "/api/registration/v1/places/search?place=Pizza&latitude=-29.366054&longitude=-50.877113"))
                        .timeout(Duration.ofSeconds(35))
                        .header("Content-Type", "application/json")
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        "{\"types\":[\"Restaurantes\"],\"ordination\":\"Mais próximo\"}"))
                        .build();

        long started = System.nanoTime();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;

        assertThat(response.statusCode()).withFailMessage(response.body()).isEqualTo(200);
        assertThat(elapsedMs).isLessThan(MAX_ELAPSED_MS);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.path("places").size()).isGreaterThan(0);
        assertPhotosStaySmall(body.path("places"));
    }

    private JsonNode get(String path) throws Exception {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + path))
                        .timeout(Duration.ofSeconds(35))
                        .GET()
                        .build();

        long started = System.nanoTime();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;

        assertThat(response.statusCode()).withFailMessage(response.body()).isEqualTo(200);
        assertThat(elapsedMs).isLessThan(MAX_ELAPSED_MS);
        return objectMapper.readTree(response.body());
    }

    private static void assertPhotosStaySmall(JsonNode places) {
        for (JsonNode place : places) {
            String photo = place.path("photo").asText("");
            assertThat(photo.length())
                    .withFailMessage(
                            place.path("name").asText()
                                    + " photo chars="
                                    + photo.length()
                                    + " max="
                                    + Place.MAX_LIST_PHOTO_BASE64_CHARS)
                    .isLessThanOrEqualTo(Place.MAX_LIST_PHOTO_BASE64_CHARS);
        }
    }
}
