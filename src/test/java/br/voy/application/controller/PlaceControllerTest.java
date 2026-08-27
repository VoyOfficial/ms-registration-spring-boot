package br.voy.application.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.voy.application.controller.request.PlaceRequest;
import br.voy.domain.entity.BusinessHours;
import br.voy.domain.entity.Interval;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.exception.CityDifferentPlaceRecommendationException;
import br.voy.domain.exception.googlePlaces.NearbyPlaceInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.NearbyPlacesZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.OverQueryLimitApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsNotFoundApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.PlacesApiClientException;
import br.voy.domain.exception.googlePlaces.RequestDeniedApiClientException;
import br.voy.domain.exception.googlePlaces.UnknownErrorApiClientException;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.service.GetNearbyPlacesService;
import br.voy.domain.service.GetPlaceDetailsService;
import br.voy.domain.service.PlaceRegistryService;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.infrastructure.agents.PlacesApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.errors.NotFoundException;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.errors.RequestDeniedException;
import com.google.maps.errors.UnknownErrorException;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceEditorialSummary;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    public static final String URL = "/v1/places";

    @Autowired MockMvc mockMvc;

    @MockBean private PlaceRepository placeRepository;

    @MockBean private PlacesApiClient placesApiClient;

    @MockBean GetNearbyPlacesService getNearbyPlacesService;

    @MockBean GetPlaceDetailsService getPlaceDetailsService;

    @MockBean PlaceRegistryService placeRegistryService;

    @MockBean GetRecommendedPlacesUseCase placeRecommendationUseCase;

    @Autowired ObjectMapper objectMapper;

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

        doReturn(nearbyPlaces)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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
        NearbyPlacesZeroResultsApiClientException expectedException =
                new NearbyPlacesZeroResultsApiClientException(googleException);

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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
        RequestDeniedApiClientException expectedException =
                new RequestDeniedApiClientException(googleException);

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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
        NearbyPlaceInvalidRequestApiClientException expectedException =
                new NearbyPlaceInvalidRequestApiClientException(googleException);

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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
        OverQueryLimitApiClientException expectedException =
                new OverQueryLimitApiClientException(googleException);

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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
        UnknownErrorApiClientException expectedException =
                new UnknownErrorApiClientException(googleException);

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
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

        PlacesApiClientException expectedException =
                new PlacesApiClientException(new RuntimeException());

        doThrow(expectedException)
                .when(getNearbyPlacesService)
                .getNearbyPlaces(any(), any(), any(), any());

        // action - validation
        mockMvc.perform(
                        get(URL).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude))
                                .param("radius", String.valueOf(radius))
                                .param("placeType", placeType)
                                .param("nextPageToken", nextPageToken))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Must to Get Place Details by Id")
    void mustToGetPlaceById() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";
        var name = "Place";
        var contact = "(54) 3286-1362";
        var about =
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.";
        var rating = 4.5f;
        var userRatingsTotal = 2599;
        var images = new String[] {"image1", "image2"};
        var address = "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil";

        var placeDetails = createPlaceDetails(placeId);

        doReturn(placeDetails).when(getPlaceDetailsService).getPlaceDetails(anyString());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googlePlaceId").value(placeId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.about").value(containsString(about)))
                .andExpect(jsonPath("$.contact").value(contact))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.userRatingsTotal").value(userRatingsTotal))
                .andExpect(jsonPath("$.photos").isNotEmpty())
                .andExpect(jsonPath("$.photos", hasSize(2)))
                .andExpect(jsonPath("$.photos[0]").value(new PlacePhoto()))
                .andExpect(jsonPath("$.photos[1]").value(new PlacePhoto()))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    @DisplayName("Don't to get place by id when zero results error occurs")
    void dontToGetPlaceByIdWhenZeroResultsErrorOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        ZeroResultsException googleException = new ZeroResultsException("");
        PlaceDetailsZeroResultsApiClientException expectedException =
                new PlaceDetailsZeroResultsApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
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
        PlaceDetailsNotFoundApiClientException expectedException =
                new PlaceDetailsNotFoundApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
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
        PlaceDetailsInvalidRequestApiClientException expectedException =
                new PlaceDetailsInvalidRequestApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
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
        OverQueryLimitApiClientException expectedException =
                new OverQueryLimitApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
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
        RequestDeniedApiClientException expectedException =
                new RequestDeniedApiClientException(googleException);

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Don't to get place by id when Api Exception occurs")
    void dontToGetPlaceByIdWhenApiExceptionOccurs() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";

        PlacesApiClientException expectedException =
                new PlacesApiClientException(new RuntimeException());

        doThrow(expectedException).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(
                        get(URL + "/" + placeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Must to Registry Recommendation Place")
    void mustToRegistryRecommendationPlace() throws Exception {

        // scenario
        var placeId = 1L;

        PlaceRequest placeRequest =
                PlaceRequest.builder()
                        .name("Hard Rock Cafe Gramado")
                        .city("Gramado")
                        .startRecommendation(LocalDateTime.now())
                        .ranking(2)
                        .build();

        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);
        var expectedLocationHeader = "http://localhost/v1/places/" + placeId;

        doReturn(placeId).when(placeRegistryService).registry(placeRequest.toDomain());

        // action - validation
        var mvcResult =
                mockMvc.perform(
                                post(URL)
                                        .content(placeRequestJson)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andReturn();

        String locationHeader = mvcResult.getResponse().getHeader("Location");

        assertEquals(expectedLocationHeader, locationHeader);
    }

    @Test
    @DisplayName("Don't should to Registry Recommendation Place when to Receive Invalid Request")
    void dontShouldToRegistryRecommendationPlaceWhenToReceiveInvalidRequest() throws Exception {

        // scenario
        PlaceRequest placeRequest = PlaceRequest.builder().build();
        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);

        // action - validation
        mockMvc.perform(post(URL).content(placeRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.city").value("must not be blank"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.ranking")
                                .value("must not be null"))
                .andReturn();
    }

    @Test
    @DisplayName("Don't should to Registry Recommendation Place")
    void dontShouldToRegistryRecommendationPlace() throws Exception {

        // scenario
        PlaceRequest placeRequest = PlaceRequest.builder().build();
        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);

        // action - validation
        mockMvc.perform(post(URL).content(placeRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.city").value("must not be blank"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.errors.ranking")
                                .value("must not be null"))
                .andReturn();
    }

    @Test
    @DisplayName(
            "Don't should to Registry Recommendation Place When City of Request is different between GooglePlace")
    void dontShouldToRegistryRecommendationPlaceWhenCityOfRequestIsDifferentBetweenGooglePlace()
            throws Exception {

        // scenario
        PlaceRequest placeRequest =
                PlaceRequest.builder()
                        .name("Hard Rock Cafe Gramado")
                        .city("Test City")
                        .startRecommendation(LocalDateTime.now())
                        .ranking(2)
                        .build();

        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);

        var expectedException = new CityDifferentPlaceRecommendationException();

        doThrow(expectedException).when(placeRegistryService).registry(any(Place.class));

        // action - validation
        mockMvc.perform(post(URL).content(placeRequestJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.error")
                                .value(
                                        "City informed is different of city registered in Google Place"))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.message")
                                .value(
                                        "The Place contains a city different of city registered in google place."))
                .andReturn();
    }

    @Test
    @DisplayName("Must to Get Recommended Places with pagination")
    void mustToGetRecommendedPlacesWithPagination() throws Exception {
        // scenario
        var latitude = -29.35995;
        var longitude = -50.84805;
        var range = 10.0;
        var pageSize = 5;
        var nextPageToken = "";

        var recommendedPlaces = createRecommendedPlacesResponse(pageSize, true);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(latitude, longitude, range, pageSize, nextPageToken);

        // action - validation
        mockMvc.perform(
                        get(URL + "/recommendations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude))
                                .param("range", String.valueOf(range))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("nextPageToken", nextPageToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.places").isNotEmpty())
                .andExpect(jsonPath("$.data.places", hasSize(5)))
                .andExpect(jsonPath("$.data.nextTokenPage").value("next-page-token-123"));
    }

    @Test
    @DisplayName("Must to Get Recommended Places without nextPageToken")
    void mustToGetRecommendedPlacesWithoutNextPageToken() throws Exception {
        // scenario
        var latitude = -29.35995;
        var longitude = -50.84805;
        var range = 10.0;
        var pageSize = 5;
        var nextPageToken = "";

        var recommendedPlaces = createRecommendedPlacesResponse(5, false);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(latitude, longitude, range, pageSize, nextPageToken);

        // action - validation
        mockMvc.perform(
                        get(URL + "/recommendations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude))
                                .param("range", String.valueOf(range))
                                .param("pageSize", String.valueOf(pageSize))
                                .param("nextPageToken", nextPageToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.places").isNotEmpty())
                .andExpect(jsonPath("$.data.places", hasSize(5)))
                .andExpect(jsonPath("$.data.nextTokenPage").doesNotExist());
    }

    @Test
    @DisplayName("Must to Get Recommended Places with default pageSize")
    void mustToGetRecommendedPlacesWithDefaultPageSize() throws Exception {
        // scenario
        var latitude = -29.35995;
        var longitude = -50.84805;
        var range = 10.0;

        var recommendedPlaces = createRecommendedPlacesResponse(5, false);

        doReturn(recommendedPlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(latitude, longitude, range, 5, "");

        // action - validation
        mockMvc.perform(
                        get(URL + "/recommendations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude))
                                .param("range", String.valueOf(range)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.places").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 404 when no recommended places found")
    void shouldReturn404WhenNoRecommendedPlacesFound() throws Exception {
        // scenario
        var latitude = -29.35995;
        var longitude = -50.84805;

        var emptyResponse =
                new br.voy.application.controller.response.RecommendedPlacesResponse(
                        new ArrayList<>(), null);

        doReturn(emptyResponse)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(latitude, longitude, null, 5, "");

        // action - validation
        mockMvc.perform(
                        get(URL + "/recommendations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should navigate to next page with nextPageToken")
    void shouldNavigateToNextPageWithNextPageToken() throws Exception {
        // scenario
        var latitude = -29.35995;
        var longitude = -50.84805;
        var nextPageToken = "page-token-from-previous-request";
        var pageSize = 5;

        var nextPagePlaces = createRecommendedPlacesResponse(5, false);

        doReturn(nextPagePlaces)
                .when(placeRecommendationUseCase)
                .getRecommendedPlaces(latitude, longitude, null, pageSize, nextPageToken);

        // action - validation
        mockMvc.perform(
                        get(URL + "/recommendations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("latitude", String.valueOf(latitude))
                                .param("longitude", String.valueOf(longitude))
                                .param("nextPageToken", nextPageToken)
                                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"))
                .andExpect(jsonPath("$.data.places").isNotEmpty());
    }

    private static br.voy.application.controller.response.RecommendedPlacesResponse
            createRecommendedPlacesResponse(int size, boolean hasNextPage) {
        List<br.voy.application.controller.response.PlaceResponse> placeResponses =
                new ArrayList<>();

        for (int i = 0; i < size; i++) {
            var place = createPlace("ChIJq6qq6oZJGZURlUgeg2eJ3b" + i, i);
            placeResponses.add(
                    br.voy.application.controller.response.PlaceResponse.fromDomain(place));
        }

        String nextToken = hasNextPage ? "next-page-token-123" : null;
        return new br.voy.application.controller.response.RecommendedPlacesResponse(
                placeResponses, nextToken);
    }

    private static NearbyPlaces createNearbyPlacesWith20Places() {

        List<Place> placeList = new ArrayList<>();

        for (int index = 0; index <= 19; index++) {
            placeList.add(createPlace("ChIJq6qq6oZJGZURlUgeg2eJ3b", index));
        }

        return new NearbyPlaces(placeList, "AZose0kJX6a...");
    }

    private static PlaceDetails createPlaceGoogleDetails(String placeId) {

        Interval interval = new Interval("12:00 AM", "11:59 PM");

        BusinessHours sunday = new BusinessHours("Sunday", interval);
        BusinessHours monday = new BusinessHours("Monday", interval);
        BusinessHours tuesday = new BusinessHours("Tuesday", interval);
        BusinessHours wednesday = new BusinessHours("Wednesday", interval);
        BusinessHours thursday = new BusinessHours("Thursday", interval);
        BusinessHours friday = new BusinessHours("Friday", interval);
        BusinessHours saturday = new BusinessHours("Saturday", interval);

        List<BusinessHours> businessHours =
                List.of(sunday, monday, tuesday, wednesday, thursday, friday, saturday);

        var images = new String[] {"image1", "image2"};

        var place = new PlaceDetails();
        place.photos = new Photo[2];
        place.photos[0] = new Photo();
        place.photos[0].photoReference = "image1";
        place.photos[1] = new Photo();
        place.photos[1].photoReference = "image2";
        place.placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";
        place.name = "Place";
        place.formattedPhoneNumber = "(54) 3286-1362";
        place.editorialSummary = new PlaceEditorialSummary();
        place.editorialSummary.overview =
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.";
        place.rating = 4.7f;
        place.userRatingsTotal = 2599;
        place.formattedAddress = "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil";

        return place;
    }

    private static Place createPlace(String id, Integer index) {

        return new Place(
                null,
                id,
                "Place" + index,
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.",
                "(54) 3286-1362",
                null, // businessHours
                4.7f,
                2599,
                false, // isSaved
                "photoReference",
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=photoReference&key=test_key", // principalPhotoUrl
                List.of(new PlacePhoto(), new PlacePhoto()),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil",
                "Gramado",
                true, // status
                1, // ranking
                null, // startRecommendation
                null, // endRecommendation
                null, // createdAt
                null, // createdDate
                null, // lastCancel
                65.2f, // distanceOfLocal
                65.2, // latitude
                65.2, // longitude
                ""); // distanceFromUserLocation
    }

    private br.voy.domain.entity.PlaceDetails createPlaceDetails(String placeId) {

        Interval interval = new Interval("12:00 AM", "11:59 PM");

        BusinessHours sunday = new BusinessHours("Sunday", interval);
        BusinessHours monday = new BusinessHours("Monday", interval);
        BusinessHours tuesday = new BusinessHours("Tuesday", interval);
        BusinessHours wednesday = new BusinessHours("Wednesday", interval);
        BusinessHours thursday = new BusinessHours("Thursday", interval);
        BusinessHours friday = new BusinessHours("Friday", interval);
        BusinessHours saturday = new BusinessHours("Saturday", interval);

        List<BusinessHours> businessHours =
                List.of(sunday, monday, tuesday, wednesday, thursday, friday, saturday);

        return br.voy.domain.entity.PlaceDetails.builder()
                .googlePlaceId(placeId)
                .name("Place")
                .about(
                        "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.")
                .contact("(54) 3286-1362")
                .userRatingsTotal(2599)
                .businessHours(businessHours)
                .rating(4.5f)
                .photos(List.of(new PlacePhoto(), new PlacePhoto()))
                .address("R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil")
                .build();
    }
}
