package src.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import src.application.controller.request.PlaceRequest;
import src.domain.entity.*;
import src.domain.exception.CityDifferentPlaceRecommendationException;
import src.domain.exception.googlePlaces.*;
import src.domain.service.GetNearbyPlacesService;
import src.domain.service.GetPlaceDetailsService;
import src.domain.service.PlaceRegistryService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @MockBean
    PlaceRegistryService placeRegistryService;

    @Autowired
    ObjectMapper objectMapper;

    @InjectMocks
    PlaceController placeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

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
    @DisplayName("Must to Get Place Details by Id")
    void mustToGetPlaceById() throws Exception {

        // scenario
        var placeId = "ChIJq6qq6oZJGZURlUgeg2eJ3b0";
        var name = "Place";
        var contact = "(54) 3286-1362";
        var about = "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.";
        var rating = 4.7f;
        var userRatingsTotal = 2599;
        var images = new String[]{"image1", "image2"};
        var address = "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil";

        var placeDetails = createPlaceDetails(placeId);

        doReturn(placeDetails).when(getPlaceDetailsService).getPlaceDetails(any());

        // action - validation
        mockMvc.perform(get(URL + "/" + placeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googlePlaceId").value(placeId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.about").value(containsString(about)))
                .andExpect(jsonPath("$.contact").value(contact))
                .andExpect(jsonPath("$.rating").value(rating))
                .andExpect(jsonPath("$.userRatingsTotal").value(userRatingsTotal))
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

    @Test
    @DisplayName("Must to Registry Recommendation Place")
    void mustToRegistryRecommendationPlace() throws Exception {

        // scenario
        var placeId = 1L;

        PlaceRequest placeRequest = PlaceRequest
                .builder()
                .name("Hard Rock Cafe Gramado")
                .city("Gramado")
                .ranking(2)
                .build();

        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);
        var expectedLocationHeader = "http://localhost/v1/places/" + placeId;

        doReturn(placeId).when(placeRegistryService).registry(placeRequest.toDomain());

        // action - validation
        var mvcResult = mockMvc.perform(
                        post(URL)
                                .content(placeRequestJson)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated())
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
        mockMvc.perform(
                        post(URL)
                                .content(placeRequestJson)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.name").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.city").value("must not be blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.ranking").value("must not be null"))
                .andReturn();

    }

    @Test
    @DisplayName("Don't should to Registry Recommendation Place When City of Request is different between GooglePlace")
    void dontShouldToRegistryRecommendationPlaceWhenCityOfRequestIsDifferentBetweenGooglePlace() throws Exception {

        // scenario
        PlaceRequest placeRequest = PlaceRequest
                .builder()
                .name("Hard Rock Cafe Gramado")
                .city("Test City")
                .ranking(2)
                .build();

        var placeRequestJson = objectMapper.writeValueAsString(placeRequest);

        var expectedException = new CityDifferentPlaceRecommendationException();

        doThrow(expectedException).when(placeRegistryService).registry(any(Place.class));

        // action - validation
        mockMvc.perform(
                        post(URL)
                                .content(placeRequestJson)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("City informed is different of city registered in Google Place"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The Place contains a city different of city registered in google place."))
                .andReturn();

    }

    private static NearbyPlaces createNearbyPlacesWith20Places() {

        List<Place> placeList = new ArrayList<>();

        for (int index = 0; index <= 19; index++) {
            placeList.add(createPlace("ChIJq6qq6oZJGZURlUgeg2eJ3b", index));
        }

        return new NearbyPlaces(placeList, "AZose0kJX6a...");

    }

    private static PlaceDetails createPlaceDetails(String placeId) {

        Interval interval = new Interval("12:00 AM", "11:59 PM");

        BusinessHours sunday = new BusinessHours("Sunday", interval);
        BusinessHours monday = new BusinessHours("Monday", interval);
        BusinessHours tuesday = new BusinessHours("Tuesday", interval);
        BusinessHours wednesday = new BusinessHours("Wednesday", interval);
        BusinessHours thursday = new BusinessHours("Thursday", interval);
        BusinessHours friday = new BusinessHours("Friday", interval);
        BusinessHours saturday = new BusinessHours("Saturday", interval);

        List<BusinessHours> businessHours = List.of(sunday, monday, tuesday, wednesday, thursday, friday, saturday);

        return new PlaceDetails(placeId,
                "Place",
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.",
                "(54) 3286-1362",
                businessHours,
                4.7f,
                2599,
                List.of("image1", "image2"),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil"
        );
    }

    private static Place createPlace(String id, Integer index) {

        return new Place(null,
                id,
                "Place" + index,
                //TODO Analisar como o preencher o campo about
                "Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.",
                "(54) 3286-1362",
                null,
                4.7f,
                2599,
                null,
                "photoReference",
                List.of("image1", "image2"),
                "R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil",
                "Gramado",
                true,
                1,
                null,
                null,
                null,
                null,
                65.2f,
                65.2f,
                65.2f);
    }

}