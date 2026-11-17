package br.voy.infrastructure.config.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import br.voy.domain.usecase.GetPlaceDetailsUseCase;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.domain.usecase.PlaceRegistryUseCase;
import br.voy.domain.usecase.SearchPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        properties = {
            "voy.security.require-api-token=true",
            "voy.security.api-access-token=",
            "spring.flyway.enabled=false",
            "spring.datasource.url=jdbc:h2:mem:api_token_misconfig;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS registration",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class ApiAccessTokenMisconfiguredTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PlaceRepository placeRepository;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean private GetNearbyPlacesUseCase getNearbyPlacesUseCase;

    @MockBean private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @MockBean private PlaceRegistryUseCase placeRegistryUseCase;

    @MockBean private GetRecommendedPlacesUseCase getRecommendedPlacesUseCase;

    @MockBean private SearchPlacesUseCase searchPlacesUseCase;

    @Test
    @DisplayName("Should return 503 when token is required but not configured")
    void shouldReturnServiceUnavailableWhenTokenMissing() throws Exception {
        mockMvc.perform(get("/v1/places").param("latitude", "-29.37").param("longitude", "-50.87"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message", containsString("not configured")));
    }
}
