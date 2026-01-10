package br.voy.domain.service;


import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import com.google.maps.model.Photo;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetNearbyPlacesServiceTest {

    public static final double LATITUDE = 2.7986896;
    public static final double LONGITUDE = -60.7532497;
    public static final String NEXT_PAGE_TOKEN = "AZose0kAquvI0OxlfS1GiCgQUr2zxeuhP_W0vjpKo9to093vL3mgI0vpTVhfNlYfKo-jka5cthTv9TJmv27TTP8wvN5qMS3VGGxoR9N6ZR_eBytNfrbCKrevuoPrFIeKwiSBxsKuVAM7LfM6xFxON1mZIZus0Qpd9claswgZKz0-Pj0WkvXoAN9KuqNzdYpyDXsBnTiwSd3aCuyXSkaN_T3JQL8IkS-GxzddSFweguWTG0IPojXqE3gTF3gHGdsTQJ2FxuxFOx3i_Hy0JMQpoolLZMDUaBgYkig8ASMLysVf-WQnF4nBeQdMwF0Dh4zl8sxLfTuE4Dk0YwArXHJklv-4oDsF6JttwZCSdilkv3XudKqpditzDjRbOeUtxenCNUAh_BSEo4nrZo2BrAww3nlkyu58Pe2MHtN8QtV6gnTd";

    @Mock
    GooglePlacesPort googlePlacesPort;

    @Mock
    PlaceRepository placeRepository;

    @InjectMocks
    GetNearbyPlacesService service;

    @Test
    @DisplayName("Must to Get All Nearby Places given a Coordinates, radius and placeType")
    void mustToGetAllNearbyGivenACoordinatesRadiusPlaceType() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // Mock repository to return empty (places don't exist yet)
        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenReturn(Optional.empty());

        // Mock repository save to return the place with an ID
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .about(place.getAbout())
                    .rating(place.getRating())
                    .userRatingsTotal(place.getUserRatingsTotal())
                    .address(place.getAddress())
                    .principalPhoto(place.getPrincipalPhoto())
                    .principalPhotoUrl(place.getPrincipalPhotoUrl())
                    .photos(place.getPhotos())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());
        assertEquals(NEXT_PAGE_TOKEN, nearbyPlaces.getNextTokenPage());

        // Verify that the service called the Google API
        verify(googlePlacesPort, times(1)).getNearbyPlaces(any(Coordinates.class), any(), anyString(), anyString());

        // Verify that each place was checked if it exists
        verify(placeRepository, times(2)).findPlaceByGooglePlaceId(anyString());

        // Verify that each place was saved
        verify(placeRepository, times(2)).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must not save places that already exist in database")
    void mustNotSavePlacesThatAlreadyExist() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();
        Place existingPlace = nearbyPlacesResponse.getPlaces().get(0);

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // First place already exists, second doesn't
        when(placeRepository.findPlaceByGooglePlaceId(existingPlace.getGooglePlaceId()))
                .thenReturn(Optional.of(existingPlace));
        when(placeRepository.findPlaceByGooglePlaceId(argThat(id -> !id.equals(existingPlace.getGooglePlaceId()))))
                .thenReturn(Optional.empty());

        // Mock save for new places
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(2L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify that only 1 place was saved (the one that didn't exist)
        verify(placeRepository, times(1)).savePlace(any(Place.class));

        // Verify that both places were checked
        verify(placeRepository, times(2)).findPlaceByGooglePlaceId(anyString());
    }

    @Test
    @DisplayName("Must handle error when saving place fails")
    void mustHandleErrorWhenSavingPlaceFails() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // Mock repository to return empty (places don't exist yet)
        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenReturn(Optional.empty());

        // First save fails, second succeeds
        when(placeRepository.savePlace(any(Place.class)))
                .thenThrow(new RuntimeException("Database error"))
                .thenAnswer(invocation -> {
                    Place place = invocation.getArgument(0);
                    return Place.builder()
                            .id(1L)
                            .googlePlaceId(place.getGooglePlaceId())
                            .name(place.getName())
                            .build();
                });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify that save was attempted for both places
        verify(placeRepository, times(2)).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must return empty list when Google API returns no places")
    void mustReturnEmptyListWhenNoPlacesFound() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces emptyResponse = new NearbyPlaces(List.of(), null);

        doReturn(emptyResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, null);

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(0, nearbyPlaces.getPlaces().size());
        Assertions.assertNull(nearbyPlaces.getNextTokenPage());

        // Verify no database operations were performed
        verify(placeRepository, never()).findPlaceByGooglePlaceId(anyString());
        verify(placeRepository, never()).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must handle null nextPageToken correctly")
    void mustHandleNullNextPageToken() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMockWithoutToken();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, null);

        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenReturn(Optional.empty());
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());
        Assertions.assertNull(nearbyPlaces.getNextTokenPage());

        verify(googlePlacesPort, times(1)).getNearbyPlaces(any(Coordinates.class), any(), anyString(), isNull());
    }

    @Test
    @DisplayName("Must handle repository exception during place lookup")
    void mustHandleRepositoryExceptionDuringLookup() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // First lookup throws exception, second returns empty
        when(placeRepository.findPlaceByGooglePlaceId(anyString()))
                .thenThrow(new RuntimeException("Database connection error"))
                .thenReturn(Optional.empty());

        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // The async method should handle the error gracefully
        verify(placeRepository, times(2)).findPlaceByGooglePlaceId(anyString());
    }

    @Test
    @DisplayName("Must propagate exception when Google Places API fails")
    void mustPropagateExceptionWhenGoogleApiIFails() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        doThrow(new RuntimeException("Google API error")).when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // action & validation
        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        });

        // Verify that no database operations were performed
        verify(placeRepository, never()).findPlaceByGooglePlaceId(anyString());
        verify(placeRepository, never()).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must handle empty places list in async save")
    void mustHandleEmptyPlacesListInAsyncSave() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "RESTAURANT";

        NearbyPlaces emptyResponse = new NearbyPlaces(List.of(), NEXT_PAGE_TOKEN);

        doReturn(emptyResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(0, nearbyPlaces.getPlaces().size());
        assertEquals(NEXT_PAGE_TOKEN, nearbyPlaces.getNextTokenPage());

        // Verify no database operations were performed
        verify(placeRepository, never()).findPlaceByGooglePlaceId(anyString());
        verify(placeRepository, never()).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must call savePlacesAsync and continue execution without waiting")
    void mustCallSavePlacesAsyncAndContinue() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 5000;
        var placeType = "MUSEUM";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, null);

        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenReturn(Optional.empty());
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // The method should return immediately without waiting for async save
        verify(googlePlacesPort, times(1)).getNearbyPlaces(coordinates, radius, placeType, null);
    }

    @Test
    @DisplayName("Must handle all places already existing in database")
    void mustHandleAllPlacesAlreadyExisting() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // All places already exist
        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenAnswer(invocation -> {
            String googlePlaceId = invocation.getArgument(0);
            return Optional.of(Place.builder()
                    .id(1L)
                    .googlePlaceId(googlePlaceId)
                    .name("Existing Place")
                    .build());
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify that no place was saved
        verify(placeRepository, never()).savePlace(any(Place.class));

        // Verify that all places were checked
        verify(placeRepository, times(2)).findPlaceByGooglePlaceId(anyString());
    }

    @Test
    @DisplayName("Must handle mixed exceptions in async save gracefully")
    void mustHandleMixedExceptionsInAsyncSave() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // First place lookup throws exception, second returns empty
        when(placeRepository.findPlaceByGooglePlaceId(anyString()))
                .thenThrow(new RuntimeException("Lookup error"))
                .thenReturn(Optional.empty());

        // Second place save succeeds
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify lookup was attempted for both places
        verify(placeRepository, times(2)).findPlaceByGooglePlaceId(anyString());

        // Verify save was only called once (second place, first failed on lookup)
        verify(placeRepository, times(1)).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Must handle zero radius parameter")
    void mustHandleZeroRadius() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 0;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = new NearbyPlaces(List.of(), null);

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, null);

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(0, nearbyPlaces.getPlaces().size());

        verify(googlePlacesPort, times(1)).getNearbyPlaces(coordinates, radius, placeType, null);
    }

    @Test
    @DisplayName("Must handle large number of places")
    void mustHandleLargeNumberOfPlaces() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 5000;
        var placeType = "RESTAURANT";

        // Create a response with many places
        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();
        PlacesSearchResult[] results = new PlacesSearchResult[20];
        for (int i = 0; i < 20; i++) {
            results[i] = createPlacesSearchResultMock("Place " + i);
        }
        placesSearchResponse.results = results;
        placesSearchResponse.nextPageToken = NEXT_PAGE_TOKEN;

        var places = Arrays.stream(placesSearchResponse.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        NearbyPlaces nearbyPlacesResponse = new NearbyPlaces(places, placesSearchResponse.nextPageToken);

        doReturn(nearbyPlacesResponse).when(googlePlacesPort).getNearbyPlaces(coordinates, radius, placeType, null);

        when(placeRepository.findPlaceByGooglePlaceId(anyString())).thenReturn(Optional.empty());
        when(placeRepository.savePlace(any(Place.class))).thenAnswer(invocation -> {
            Place place = invocation.getArgument(0);
            return Place.builder()
                    .id(1L)
                    .googlePlaceId(place.getGooglePlaceId())
                    .name(place.getName())
                    .build();
        });

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(20, nearbyPlaces.getPlaces().size());

        // Verify all places were processed
        verify(placeRepository, times(20)).findPlaceByGooglePlaceId(anyString());
        verify(placeRepository, times(20)).savePlace(any(Place.class));
    }

    private NearbyPlaces createNearbyPlacesMock() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1");
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2");

        placesSearchResponse.results = Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);
        placesSearchResponse.nextPageToken = NEXT_PAGE_TOKEN;

        var places = Arrays.stream(placesSearchResponse.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        return new NearbyPlaces(places, placesSearchResponse.nextPageToken);

    }

    private NearbyPlaces createNearbyPlacesMockWithoutToken() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1");
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2");

        placesSearchResponse.results = Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);
        placesSearchResponse.nextPageToken = null;

        var places = Arrays.stream(placesSearchResponse.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        return new NearbyPlaces(places, null);
    }

    private PlacesSearchResult createPlacesSearchResultMock(String name) {

        PlacesSearchResult result = new PlacesSearchResult();

        result.name = name;
        result.placeId = "ChIJHzIEeEIyGZURpq7lgfAlHKc" + name;
        result.rating = 4.5f;
        result.types = new String[]{"CAFE"};
        result.userRatingsTotal = 2;

        Photo photo = new Photo();
        photo.photoReference = "AZose0lqcLLyqLLzqoBkMpKb8ZkgqfmWhiAJu3plnLYwn5ncir8RXu4PFjvEbSRkYwUzw8SXRRLmFTtVRxbJObSAvuyjQsCvtnhm7PZyOLgeynlgXDor0SjTjFS0wa-y7m3WSgeus861Af8ZIRpKBtbvziFcT8sK0a31A8lqEME-e6JYJY_4";
        result.photos = new Photo[]{photo};

        return result;

    }

}
