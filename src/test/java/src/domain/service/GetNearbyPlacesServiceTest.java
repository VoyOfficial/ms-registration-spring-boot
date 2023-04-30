package src.domain.service;


import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.application.providers.adapters.GooglePlacesAPIAdapter;
import src.domain.entity.Coordinates;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetNearbyPlacesServiceTest {

    public static final double LATITUDE = 2.7986896;
    public static final double LONGITUDE = -60.7532497;

    @Mock
    GooglePlacesAPIAdapter placesAdapter;

    @InjectMocks
    GetAllNearbyPlacesService service;

    @Test
    @DisplayName("Must to Get All Nearby Establishments given a Coordinates")
    void mustToGetAllNearbyEstablishmentGivenALocation() {

        // cenary
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        PlacesSearchResponse placesSearchResponse = createPlacesSearchResponseMock();

        when(placesAdapter.getNearbyPlaces(any(Coordinates.class), any(), anyString()))
                .thenReturn(placesSearchResponse);

        // action
        var nearbyEstablishments = service.getNearbyPlaces(coordinates, radius, placeType);

        // validation
        assertNotNull(nearbyEstablishments);

    }

    private PlacesSearchResponse createPlacesSearchResponseMock() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1", LATITUDE, LONGITUDE);
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2", LATITUDE, LONGITUDE);

        placesSearchResponse.results = Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);

        return placesSearchResponse;

    }

    private PlacesSearchResult createPlacesSearchResultMock(String name, double latitude, double longitude) {

        PlacesSearchResult result = new PlacesSearchResult();

        result.name = name;
        result.geometry = new Geometry();
        result.geometry.location = new LatLng(latitude, longitude);

        return result;

    }

}
