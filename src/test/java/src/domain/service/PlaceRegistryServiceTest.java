package src.domain.service;

import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import src.domain.entity.Place;
import src.domain.exception.PlaceAlreadyExistsException;
import src.domain.repository.PlaceRepository;
import src.infrastructure.agents.PlacesApiClient;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PlaceRegistryServiceTest {

    @Mock
    private PlaceRepository repository;

    @Mock
    private PlacesApiClient placesApiClient;

    @InjectMocks
    private PlaceRegistryService placeRegistryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registry_NewPlace_ShouldSaveSuccessfully() {
        // Arrange
        Place place = new Place();
        place.setId(123L);
        place.setName("Test Place");
        place.setCity("Test City");
        place.setStartRecommendation(new Date());

        PlaceDetails placeDetails = placeDetailsGenerator();

        when(placesApiClient.getPlaceFromText("Test Place", "Test City")).thenReturn(placeDetails);
        when(repository.findByGooglePlaceId(anyString())).thenReturn(false);
        when(repository.savePlace(any())).thenReturn(place);

        // Act
        Long savedId = placeRegistryService.registry(place);

        // Assert
        // Verify that the repository save method was called once
        verify(repository, times(1)).savePlace(any());
        // Verify that the returned ID is not null
        assertNotNull(savedId);
    }

    @Test
    void registry_PlaceAlreadyExists_ShouldThrowException() {
        // Arrange
        Place place = new Place();
        place.setId(123L);
        place.setName("Test Place");
        place.setCity("Test City");
        place.setStartRecommendation(new Date());

        PlaceDetails placeDetails = placeDetailsGenerator();

        when(placesApiClient.getPlaceFromText("Test Place", "Test City")).thenReturn(placeDetails);
        when(repository.findByGooglePlaceId(anyString())).thenReturn(true);

        // Act and Assert
        // Verify that PlaceAlreadyExistsException is thrown
        assertThrows(PlaceAlreadyExistsException.class, () -> placeRegistryService.registry(place));
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
        placeDetails.servesDinner = true;
        placeDetails.servesLunch = true;
        placeDetails.servesVegetarianFood = false;
        placeDetails.servesWine = true;
        placeDetails.wheelchairAccessibleEntrance = true;


        return placeDetails;
    }

}
