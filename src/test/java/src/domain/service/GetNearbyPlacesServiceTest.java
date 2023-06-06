package src.domain.service;


import com.google.maps.model.Photo;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.domain.entity.Coordinates;
import src.domain.ports.PlacesApiPort;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetNearbyPlacesServiceTest {

    public static final double LATITUDE = 2.7986896;
    public static final double LONGITUDE = -60.7532497;
    public static final String NEXT_PAGE_TOKEN = "AZose0kAquvI0OxlfS1GiCgQUr2zxeuhP_W0vjpKo9to093vL3mgI0vpTVhfNlYfKo-jka5cthTv9TJmv27TTP8wvN5qMS3VGGxoR9N6ZR_eBytNfrbCKrevuoPrFIeKwiSBxsKuVAM7LfM6xFxON1mZIZus0Qpd9claswgZKz0-Pj0WkvXoAN9KuqNzdYpyDXsBnTiwSd3aCuyXSkaN_T3JQL8IkS-GxzddSFweguWTG0IPojXqE3gTF3gHGdsTQJ2FxuxFOx3i_Hy0JMQpoolLZMDUaBgYkig8ASMLysVf-WQnF4nBeQdMwF0Dh4zl8sxLfTuE4Dk0YwArXHJklv-4oDsF6JttwZCSdilkv3XudKqpditzDjRbOeUtxenCNUAh_BSEo4nrZo2BrAww3nlkyu58Pe2MHtN8QtV6gnTd";

    @Mock
    PlacesApiPort placesApiPort;

    @InjectMocks
    GetNearbyPlacesService service;

    @Test
    @DisplayName("Must to Get All Nearby Places given a Coordinates, radius and placeType")
    void mustToGetAllNearbyGivenACoordinatesRadiusPlaceType() {

        // cenary
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        PlacesSearchResponse placesSearchResponse = createPlacesSearchResponseMock();

        doReturn(placesSearchResponse).when(placesApiPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());
        assertEquals(NEXT_PAGE_TOKEN, nearbyPlaces.getNextTokenPage());
        verify(placesApiPort, times(1)).getNearbyPlaces(any(Coordinates.class), any(), anyString(), anyString());

    }

    private PlacesSearchResponse createPlacesSearchResponseMock() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1");
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2");

        placesSearchResponse.results = Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);
        placesSearchResponse.nextPageToken = NEXT_PAGE_TOKEN;
        return placesSearchResponse;

    }

    private PlacesSearchResult createPlacesSearchResultMock(String name) {

        PlacesSearchResult result = new PlacesSearchResult();

        result.name = name;
        result.placeId = "ChIJHzIEeEIyGZURpq7lgfAlHKc";
        result.rating = 4.5f;
        result.types = new String[]{"CAFE"};
        result.userRatingsTotal = 2;

        Photo photo = new Photo();
        photo.photoReference = "AZose0lqcLLyqLLzqoBkMpKb8ZkgqfmWhiAJu3plnLYwn5ncir8RXu4PFjvEbSRkYwUzw8SXRRLmFTtVRxbJObSAvuyjQsCvtnhm7PZyOLgeynlgXDor0SjTjFS0wa-y7m3WSgeus861Af8ZIRpKBtbvziFcT8sK0a31A8lqEME-e6JYJY_4";
        result.photos = new Photo[]{photo};

        return result;

    }

}
