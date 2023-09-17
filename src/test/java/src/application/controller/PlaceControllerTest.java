package src.application.controller;

import com.google.maps.errors.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.Place;
import src.domain.exception.googlePlaces.*;
import src.domain.service.GetNearbyPlacesService;
import src.domain.service.GetPlaceDetailsService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    public static final String URL = "/v1/places";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GetNearbyPlacesService getNearbyPlacesService;

    @MockBean
    GetPlaceDetailsService getPlaceDetailsService;

    @Test
    @DisplayName("Must to Get 20 Nearby Places")
    void mustToGet20NearbyPlaces() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        var nearbyPlaces = createNearbyPlacesWith20Places();

        doReturn(nearbyPlaces).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.places").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.places", hasSize(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[0].name").value(containsString("Place" + 0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[1].name").value(containsString("Place" + 1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[2].name").value(containsString("Place" + 2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[3].name").value(containsString("Place" + 3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[4].name").value(containsString("Place" + 4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[5].name").value(containsString("Place" + 5)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[6].name").value(containsString("Place" + 6)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[7].name").value(containsString("Place" + 7)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[8].name").value(containsString("Place" + 8)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[9].name").value(containsString("Place" + 9)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[10].name").value(containsString("Place" + 10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[11].name").value(containsString("Place" + 11)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[12].name").value(containsString("Place" + 12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[13].name").value(containsString("Place" + 13)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[13].name").value(containsString("Place" + 13)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[14].name").value(containsString("Place" + 14)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[15].name").value(containsString("Place" + 15)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[16].name").value(containsString("Place" + 16)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[17].name").value(containsString("Place" + 17)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[18].name").value(containsString("Place" + 18)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.places[19].name").value(containsString("Place" + 19)));

    }

    @Test
    @DisplayName("Don't to get nearby places when zero results error occurs")
    void dontToGetNearbyPlacesWhenZeroResultsErrorOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        ZeroResultsException googleException = new ZeroResultsException("");
        NearbyPlacesZeroResultsApiClientException expectedException = new NearbyPlacesZeroResultsApiClientException(googleException);

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Don't to get nearby places when request denied error occurs")
    void dontToGetNearbyPlacesWhenRequestDeniedErrorOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        RequestDeniedException googleException = new RequestDeniedException("");
        RequestDeniedApiClientException expectedException = new RequestDeniedApiClientException(googleException);

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Don't to get nearby places when invalid request error occurs")
    void dontToGetNearbyPlacesWhenInvalidRequestErrorOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        InvalidRequestException googleException = new InvalidRequestException("");
        NearbyPlaceInvalidRequestApiClientException expectedException = new NearbyPlaceInvalidRequestApiClientException(googleException);

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    @DisplayName("Don't to get nearby places when over query limit error occurs")
    void dontToGetNearbyPlacesWhenOverQueryLimitErrorOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        OverQueryLimitException googleException = new OverQueryLimitException("");
        OverQueryLimitApiClientException expectedException = new OverQueryLimitApiClientException(googleException);

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isTooManyRequests());

    }

    @Test
    @DisplayName("Don't to get nearby places when unknown error occurs")
    void dontToGetNearbyPlacesWhenUnknownErrorOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        UnknownErrorException googleException = new UnknownErrorException("");
        UnknownErrorApiClientException expectedException = new UnknownErrorApiClientException(googleException);

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isInternalServerError());

    }

    @Test
    @DisplayName("Don't to get nearby places when Api Exception occurs")
    void dontToGetNearbyPlacesWhenApiExceptionOccurs() throws Exception {

        // scenario
        var latitude = -29.366054;
        var longitude = -50.877113;
        var radius = 5000;
        var placeType = "shopping_mall";
        var nextPageToken = "AZose0kJX6a...";

        PlacesApiClientException expectedException = new PlacesApiClientException(new RuntimeException());

        doThrow(expectedException).when(getNearbyPlacesService).getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("latitude", String.valueOf(latitude))
                        .param("longitude", String.valueOf(longitude))
                        .param("radius", String.valueOf(radius))
                        .param("placeType", placeType)
                        .param("nextPageToken", nextPageToken))
                .andExpect(status().isInternalServerError());

    }

    private static NearbyPlaces createNearbyPlacesWith20Places() {

        List<Place> placeList = new ArrayList<>();

        for (int index = 0; index <= 19; index++) {
            placeList.add(createPlace(index));
        }

        return new NearbyPlaces(placeList, "AZose0kJX6a...");

    }

    private static Place createPlace(int id) {

        return new Place(null,
                "ChIJq6qq6oZJGZURlUgeg2eJ3b" + id,
                "Place" + id,
                List.of("lodging", "point_of_interest", "establishment"),
                null,
                null,
                4.7f,
                2599,
                null,
                "photoReference",
                List.of("image1"),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil",
                65.2f);
    }

}