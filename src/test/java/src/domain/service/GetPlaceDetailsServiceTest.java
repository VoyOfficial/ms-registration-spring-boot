package src.domain.service;

import com.google.maps.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import src.domain.entity.Place;
import src.domain.ports.PlacesApiPort;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetPlaceDetailsServiceTest {

    @Mock
    private PlacesApiPort placesApiPort;

    @InjectMocks
    private GetPlaceDetailsService getPlaceDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPlaceDetails() {
        // Mocking the response from the PlacesApiPort
        PlaceDetails placeDetailsGoogle = placeDetailsGenerator();
        when(placesApiPort.getPlaceDetails(anyString())).thenReturn(placeDetailsGoogle);

        // Invoking the method under test
        String placeId = "ChIJPQmNhEMyGZURxuHk44vIaIw";
        Place actualPlaceDetails = getPlaceDetailsService.getPlaceDetails(placeId);

        // Asserting the result
        assertNotNull(actualPlaceDetails);
    }

    PlaceDetails placeDetailsGenerator() {
        PlaceDetails placeDetails = new PlaceDetails();

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
