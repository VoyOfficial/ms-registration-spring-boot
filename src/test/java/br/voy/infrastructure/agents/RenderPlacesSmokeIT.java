package br.voy.infrastructure.agents;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Live checks against the deployed registration service. Opt-in because every run calls the real
 * Render instance and search persists places. Run with {@code RUN_RENDER_IT=true ./mvnw
 * -Dtest=RenderPlacesSmokeIT test}.
 */
@EnabledIfEnvironmentVariable(named = "RUN_RENDER_IT", matches = "(?i)true|1|yes")
class RenderPlacesSmokeIT {

    private static final String BASE_URL =
            System.getenv().getOrDefault("RENDER_BASE_URL", "https://ms-registration.onrender.com");

    private static final int MAX_LIST_PHOTO_CHARS = 200_000;

    private static final long MAX_ELAPSED_MS = 30_000;

    private final HttpClient httpClient =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Nearby list on Render returns within 30 seconds")
    void nearbyListReturnsWithinThirtySeconds() throws Exception {
        JsonNode body = get("/api/registration/v1/places?latitude=-29.385436&longitude=-50.877608");

        assertThat(body.path("places").isArray()).isTrue();
        assertThat(body.path("places").size()).isGreaterThan(0);
        assertPhotosStaySmall(body.path("places"));
    }

    @Test
    @DisplayName("Recommendations on Render return within 30 seconds")
    void recommendationsReturnWithinThirtySeconds() throws Exception {
        JsonNode body =
                get(
                        "/api/registration/v1/places/recommendations?latitude=-29.385436&longitude=-50.877608&pageSize=5");

        assertThat(body.path("places").isArray()).isTrue();
        assertThat(body.path("places").size()).isGreaterThan(0);
        assertPhotosStaySmall(body.path("places"));
    }

    @Test
    @DisplayName("Pizza search on Render returns within 30 seconds")
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
                    .withFailMessage(place.path("name").asText())
                    .isLessThan(MAX_LIST_PHOTO_CHARS);
        }
    }
}
