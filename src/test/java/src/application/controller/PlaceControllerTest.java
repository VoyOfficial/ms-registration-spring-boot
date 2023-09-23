package src.application.controller;

import com.google.maps.errors.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
                .andExpect(jsonPath("$.places").isNotEmpty())
                .andExpect(jsonPath("$.places", hasSize(20)))
                .andExpect(jsonPath("$.places[0].name").value(containsString("Place" + 0)))
                .andExpect(jsonPath("$.places[1].name").value(containsString("Place" + 1)))
                .andExpect(jsonPath("$.places[2].name").value(containsString("Place" + 2)))
                .andExpect(jsonPath("$.places[3].name").value(containsString("Place" + 3)))
                .andExpect(jsonPath("$.places[4].name").value(containsString("Place" + 4)))
                .andExpect(jsonPath("$.places[5].name").value(containsString("Place" + 5)))
                .andExpect(jsonPath("$.places[6].name").value(containsString("Place" + 6)))
                .andExpect(jsonPath("$.places[7].name").value(containsString("Place" + 7)))
                .andExpect(jsonPath("$.places[8].name").value(containsString("Place" + 8)))
                .andExpect(jsonPath("$.places[9].name").value(containsString("Place" + 9)))
                .andExpect(jsonPath("$.places[10].name").value(containsString("Place" + 10)))
                .andExpect(jsonPath("$.places[11].name").value(containsString("Place" + 11)))
                .andExpect(jsonPath("$.places[12].name").value(containsString("Place" + 12)))
                .andExpect(jsonPath("$.places[13].name").value(containsString("Place" + 13)))
                .andExpect(jsonPath("$.places[13].name").value(containsString("Place" + 13)))
                .andExpect(jsonPath("$.places[14].name").value(containsString("Place" + 14)))
                .andExpect(jsonPath("$.places[15].name").value(containsString("Place" + 15)))
                .andExpect(jsonPath("$.places[16].name").value(containsString("Place" + 16)))
                .andExpect(jsonPath("$.places[17].name").value(containsString("Place" + 17)))
                .andExpect(jsonPath("$.places[18].name").value(containsString("Place" + 18)))
                .andExpect(jsonPath("$.places[19].name").value(containsString("Place" + 19)));

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

    @Test
    @DisplayName("Must to Get Place by Id")
    void mustToGetPlaceById() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";
        var name = "Place0";
        var contact = "(54) 3286-1362";
        var rating = 4.7f;
        var about = new String[]{"Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools."};
        var userRatingsTotal = 2599;
        var distanceOfLocal = 0.0;
        var images = new String[]{"image1", "image2"};
        var address = "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil";

        var placeDetails = createPlace(placeId, 0);

        doReturn(placeDetails).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googlePlaceId").value(placeId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.about[0]").value(containsString(about[0])))
                .andExpect(jsonPath("$.contact").value(contact))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.userRatingsTotal").value(userRatingsTotal))
                .andExpect(jsonPath("$.distanceOfLocal").value(distanceOfLocal))
                .andExpect(jsonPath("$.images").isNotEmpty())
                .andExpect(jsonPath("$.images", hasSize(2)))
                .andExpect(jsonPath("$.images[0]").value(containsString(images[0])))
                .andExpect(jsonPath("$.images[1]").value(containsString(images[1])))
                .andExpect(jsonPath("$.address").value(address));

    }

    @Test
    @DisplayName("Don't to get place by id when zero results error occurs")
    void dontToGetPlaceByIdWhenZeroResultsErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        ZeroResultsException googleException = new ZeroResultsException("");
        PlaceDetailsZeroResultsApiClientException expectedException = new PlaceDetailsZeroResultsApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Don't to get place by id when Not Found error occurs")
    void dontToGetPlaceByIdWhenNotFoundErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        NotFoundException googleException = new NotFoundException("");
        PlaceDetailsNotFoundApiClientException expectedException = new PlaceDetailsNotFoundApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Don't to get place by id when invalid request error occurs")
    void dontToGetPlaceByIdWhenInvalidRequestErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        InvalidRequestException googleException = new InvalidRequestException("");
        PlaceDetailsInvalidRequestApiClientException expectedException = new PlaceDetailsInvalidRequestApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    @DisplayName("Don't to get place by id when over query limit error occurs")
    void dontToGetPlaceByIdWhenOverQueryLimitErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        OverQueryLimitException googleException = new OverQueryLimitException("");
        OverQueryLimitApiClientException expectedException = new OverQueryLimitApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());

    }

    @Test
    @DisplayName("Don't to get place by id when request denied error occurs")
    void dontToGetPlaceByIdWhenRequestDeniedErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        RequestDeniedException googleException = new RequestDeniedException("");
        RequestDeniedApiClientException expectedException = new RequestDeniedApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("Don't to get place by id when Api Exception occurs")
    void dontToGetPlaceByIdWhenApiExceptionOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        PlacesApiClientException expectedException = new PlacesApiClientException(new RuntimeException());

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

    }

    private static NearbyPlaces createNearbyPlacesWith20Places() {

        List<Place> placeList = new ArrayList<>();

        for (int index = 0; index <= 19; index++) {
            placeList.add(createPlace("ChIJq6qq6oZJGZURlUgeg2eJ3b", index));
        }

        return new NearbyPlaces(placeList, "AZose0kJX6a...");

    }

    private static Place createPlace(String id, Integer index) {

        return new Place(null,
                id,
                "Place" + index,
                List.of("Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools."),
                "(54) 3286-1362",
                null,
                4.7f,
                2599,
                null,
                "photoReference",
                List.of("image1", "image2"),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil",
                65.2f);
    }

}