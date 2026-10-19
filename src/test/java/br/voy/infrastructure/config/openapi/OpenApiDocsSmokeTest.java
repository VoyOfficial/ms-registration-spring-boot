package br.voy.infrastructure.config.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.datasource.url=jdbc:h2:mem:openapi_docs;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS registration",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class OpenApiDocsSmokeTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("OpenAPI v1 group exports Place and User paths for mobile clients")
    void apiDocsExposePlaceAndUserEndpoints() throws Exception {
        MvcResult result =
                mockMvc.perform(get("/v3/api-docs/v1")).andExpect(status().isOk()).andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(root.path("info").path("title").asText()).isEqualTo("MS Registration");
        assertThat(root.path("info").path("description").asText())
                .contains("Places")
                .contains("Users")
                .doesNotContain("User CRUD operations");

        JsonNode paths = root.path("paths");
        assertThat(paths.has("/v1/places")).isTrue();
        assertThat(paths.has("/v1/places/{placeId}")).isTrue();
        assertThat(paths.has("/v1/places/recommendations")).isTrue();
        assertThat(paths.has("/v1/users")).isTrue();
        assertThat(paths.has("/v1/users/{userId}")).isTrue();

        JsonNode schemas = root.path("components").path("schemas");
        assertThat(schemas.has("PlaceResponse")).isTrue();
        assertThat(schemas.has("NearbyPlacesResponse")).isTrue();
        assertThat(schemas.has("PlaceDetailsResponse")).isTrue();
        assertThat(schemas.has("UserResponse")).isTrue();
        assertThat(schemas.has("StandardError")).isTrue();
    }
}
