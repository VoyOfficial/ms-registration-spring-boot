package src.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.domain.entity.Place;
import src.domain.exception.googlePlaces.*;
import src.domain.ports.GooglePlacesPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class GetPlaceDetailsServiceTest {

    @Mock
    GooglePlacesPort googlePlacesPort;

    @InjectMocks
    GetPlaceDetailsService service;

    @Test
    @DisplayName("Must to Get Place Details given a placeId")
    void mustToGetPlaceDetailsGivenAPlaceId() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedPlaceDetails = createPlaceDetails(placeId);
        doReturn(expectedPlaceDetails).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        Place placeDetails = service.getPlaceDetails(placeId);

        // validation
        assertNotNull(placeDetails);
        assertEquals(expectedPlaceDetails, placeDetails);

    }

    @Test
    @DisplayName("Should throw PlaceDetailsZeroResultsApiClientException when GooglePlacePort returns zero results")
    void shouldThrowPlaceDetailsZeroResultsApiClientExceptionWhenGooglePlacePortReturnsZeroResults() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new PlaceDetailsZeroResultsApiClientException();
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(PlaceDetailsZeroResultsApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    @Test
    @DisplayName("Should throw PlaceDetailsNotFoundApiClientException when GooglePlacePort returns not found")
    void shouldThrowPlaceDetailsNotFoundApiClientExceptionWhenGooglePlacePortReturnsNotFound() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new PlaceDetailsNotFoundApiClientException();
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(PlaceDetailsNotFoundApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    @Test
    @DisplayName("Should throw PlaceDetailsInvalidRequestApiClientException when GooglePlacePort returns invalid request")
    void shouldThrowPlaceDetailsInvalidRequestApiClientExceptionWhenGooglePlacePortReturnsInvalidRequest() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new PlaceDetailsInvalidRequestApiClientException();
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(PlaceDetailsInvalidRequestApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    @Test
    @DisplayName("Should throw OverQueryLimitApiClientException when GooglePlacePort returns Over Query Limit")
    void shouldThrowOverQueryLimitApiClientExceptionWhenGooglePlacePortReturnsOverQueryLimit() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new OverQueryLimitApiClientException();
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(OverQueryLimitApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    @Test
    @DisplayName("Should throw RequestDeniedApiClientException when GooglePlacePort returns Request Denied")
    void shouldThrowRequestDeniedApiClientExceptionWhenGooglePlacePortReturnsOverQueryLimit() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new RequestDeniedApiClientException();
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(RequestDeniedApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    @Test
    @DisplayName("Should throw PlacesApiClientException when GooglePlacePort returns Unexpect Error")
    void shouldThrowPlacesApiClientExceptionWhenGooglePlacePortReturnsUnexpectError() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedException = new PlacesApiClientException("Unexpected Error");
        doThrow(expectedException).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        var raisedException = assertThrows(PlacesApiClientException.class, () ->
                service.getPlaceDetails(placeId)
        );

        // validation
        assertNotNull(raisedException);
        assertEquals(raisedException.getClass(), expectedException.getClass());
        assertEquals(raisedException.getMessage(), expectedException.getMessage());

    }

    private Place createPlaceDetails(String placeId) {

        return Place
                .builder()
                .id(null)
                .googlePlaceId(placeId)
                .name("Restaurant Fictício")
                .about(List.of("This is a Brazilian Restaurant"))
                .contact("123456789")
                .businessHours(null)
                .rating(4.5f)
                .isSaved(false)
                .photoReference("photo_reference_1")
                .images(List.of("photo_reference_1"))
                .address("123 Rua Fictícia, Cidade Fictícia")
                .distanceOfLocal(0.0f)
                .build();

    }

}