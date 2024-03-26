package src.domain.service;

import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceEditorialSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import src.domain.entity.BusinessHours;
import src.domain.entity.Interval;
import src.domain.entity.Place;
import src.domain.entity.PlaceDetails;
import src.domain.exception.googlePlaces.*;
import src.domain.ports.GooglePlacesPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPlaceDetailsServiceTest {

    @Mock
    private PlacesApiPort placesApiPort;

    @Mock
    GooglePlacesPort googlePlacesPort;

    @InjectMocks
    GetPlaceDetailsService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Must to Get Place Details given a placeId")
    void mustToGetPlaceDetailsGivenAPlaceId() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedPlaceDetails = createPlaceDetails(placeId);
        doReturn(expectedPlaceDetails).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        PlaceDetails placeDetails = service.getPlaceDetails(placeId);

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

    private PlaceDetails createPlaceDetails(String placeId) {

        Interval interval = new Interval("12:00 AM", "11:59 PM");

        BusinessHours sunday = new BusinessHours("Sunday", interval);
        BusinessHours monday = new BusinessHours("Monday", interval);
        BusinessHours tuesday = new BusinessHours("Tuesday", interval);
        BusinessHours wednesday = new BusinessHours("Wednesday", interval);
        BusinessHours thursday = new BusinessHours("Thursday", interval);
        BusinessHours friday = new BusinessHours("Friday", interval);
        BusinessHours saturday = new BusinessHours("Saturday", interval);

        List<BusinessHours> businessHours = List.of(sunday, monday, tuesday, wednesday, thursday, friday, saturday);


        return PlaceDetails
                .builder()
                .googlePlaceId(placeId)
                .name("Place")
                .about("Casual rooms in a tranquil hotel offering dining, a bar & mini-golf, plus indoor & outdoor pools.")
                .contact("(54) 3286-1362")
                .businessHours(businessHours)
                .rating(4.5f)
                .images(List.of("image1", "image2"))
                .address("R. da Bavária, 543 - Bavária, Gramado - RS, 95670-000, Brazil")
                .build();

    }

    @Test
    public void testGetPlaceDetails() {
        // Mocking the response from the PlacesApiPort
        com.google.maps.model.PlaceDetails placeDetailsGoogle = placeDetailsGenerator();
        when(placesApiPort.getPlaceDetails(anyString())).thenReturn(placeDetailsGoogle);

        // Invoking the method under test
        String placeId = "ChIJPQmNhEMyGZURxuHk44vIaIw";
        Place actualPlaceDetails = service.getPlaceDetails(placeId);

        // Asserting the result
        assertNotNull(actualPlaceDetails);
    }

    com.google.maps.model.PlaceDetails placeDetailsGenerator() {
        com.google.maps.model.PlaceDetails placeDetails = new com.google.maps.model.PlaceDetails();

        Geometry geometry = new Geometry();
        geometry.location = new LatLng(-29.38101710,-50.87110530);

        placeDetails.name = "Hard Rock Cafe";
        placeDetails.placeId = "ChIJPQmNhEMyGZURxuHk44vIaIw";
        placeDetails.formattedAddress = "R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil";
        placeDetails.adrAddress = "<span class=\"street-address\">R. Wilma Dinnebier</span> - <span class=\"extended-address\">Bairro Belverede</span>, <span class=\"locality\">Gramado</span> - <span class=\"region\">RS</span>, <span class=\"postal-code\">95670-192</span>, <span class=\"country-name\">Brazil</span>";
        placeDetails.businessStatus = "OPERATIONAL";
        placeDetails.curbsidePickup = false;
        placeDetails.geometry = geometry;
        placeDetails.photos = new Photo[0];
        placeDetails.vicinity = "Rua Wilma Dinnebier - Bairro Belverede, Gramado";
        placeDetails.formattedPhoneNumber = "(54) 3286-4040";
        placeDetails.internationalPhoneNumber = "+55 54 3286-4040";
        placeDetails.icon = null;
        placeDetails.openingHours = null;
        placeDetails.utcOffset = -180;
        placeDetails.userRatingsTotal = 13611;
        placeDetails.reservable = true;
        placeDetails.servesBeer = true;
        placeDetails.servesBreakfast = false;
        placeDetails.editorialSummary = new PlaceEditorialSummary();
        placeDetails.editorialSummary.overview = "";
        placeDetails.servesDinner = true;
        placeDetails.servesLunch = true;
        placeDetails.servesVegetarianFood = false;
        placeDetails.servesWine = true;
        placeDetails.wheelchairAccessibleEntrance = true;

        return placeDetails;
    }

}