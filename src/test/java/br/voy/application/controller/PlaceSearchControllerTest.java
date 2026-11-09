package br.voy.application.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.service.SearchPlacesService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration",
            "spring.datasource.url=jdbc:h2:mem:place_search_controller;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;INIT=CREATE SCHEMA IF NOT EXISTS registration",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class PlaceSearchControllerTest {

    private static final String SEARCH_URL = "/v1/places/search";

    @Autowired MockMvc mockMvc;

    @MockBean private PlaceRepository placeRepository;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean GetNearbyPlacesService getNearbyPlacesService;

    @MockBean GetPlaceDetailsService getPlaceDetailsService;

    @MockBean PlaceRegistryService placeRegistryService;

    @MockBean GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @MockBean SearchPlacesService searchPlacesService;

    @Test
    @DisplayName("Must return 200 search with NearbyPlacesResponse shape")
    void mustReturn200SearchWithNearbyPlacesResponseShape() throws Exception {
        var places =
                List.of(
                        Place.builder()
                                .id(12L)
                                .googlePlaceId("ChIJSearch1")
                                .name("Hard Rock Cafe")
                                .address("Av. Borges de Medeiros, Gramado")
                                .city("Gramado")
                                .rating(4.5f)
                                .userRatingsTotal(128)
                                .principalPhoto("photo-ref")
                                .latitude(-29.37)
                                .longitude(-50.88)
                                .distanceFromUserLocation("1.200 km")
                                .build());
        doReturn(new NearbyPlaces(places, "next-token"))
                .when(searchPlacesService)
                .searchPlaces(anyString(), any(), any(), any(), any());

        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("place", "Hard Rock")
                                .param("latitude", "-29.366054")
                                .param("longitude", "-50.877113")
                                .content(
                                        "{\"types\":[\"Restaurantes\"],\"ordination\":\"Mais avaliados\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places", hasSize(1)))
                .andExpect(jsonPath("$.places[0].name").value("Hard Rock Cafe"))
                .andExpect(jsonPath("$.places[0].address").value("Av. Borges de Medeiros, Gramado"))
                .andExpect(jsonPath("$.places[0].city").value("Gramado"))
                .andExpect(jsonPath("$.places[0].rating").value(4.5))
                .andExpect(jsonPath("$.places[0].userRatingsTotal").value(128))
                .andExpect(jsonPath("$.places[0].distanceFromUserLocation").value("1.200 km"))
                .andExpect(jsonPath("$.nextTokenPage").value("next-token"));
    }

    @Test
    @DisplayName("Must return 204 when search has zero results")
    void mustReturn204WhenSearchHasZeroResults() throws Exception {
        doReturn(new NearbyPlaces(List.of(), null))
                .when(searchPlacesService)
                .searchPlaces(anyString(), any(), any(), any(), any());

        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("place", "zzzz")
                                .param("latitude", "-29.366054")
                                .param("longitude", "-50.877113"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Must return 200 empty when search continuation has no items")
    void mustReturn200EmptyWhenSearchContinuationHasNoItems() throws Exception {
        doReturn(new NearbyPlaces(List.of(), null))
                .when(searchPlacesService)
                .searchPlaces(anyString(), any(), any(), any(), any());

        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("place", "Hard")
                                .param("latitude", "-29.366054")
                                .param("longitude", "-50.877113")
                                .param("nextPageToken", "some-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places", hasSize(0)))
                .andExpect(jsonPath("$.nextTokenPage").doesNotExist());
    }

    @Test
    @DisplayName("Must return 400 when search missing latitude or longitude")
    void mustReturn400WhenSearchMissingLatitudeOrLongitude() throws Exception {
        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("place", "Hard Rock")
                                .param("longitude", "-50.877113"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("place", "Hard Rock")
                                .param("latitude", "-29.366054"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post(SEARCH_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", "-29.366054")
                                .param("longitude", "-50.877113"))
                .andExpect(status().isBadRequest());
    }
}
