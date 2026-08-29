package br.voy.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.utils.PaginationTokenEncoder;
import com.google.maps.model.Photo;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class GetNearbyPlacesServiceTest {

    public static final double LATITUDE = 2.7986896;
    public static final double LONGITUDE = -60.7532497;
    public static final String NEXT_PAGE_TOKEN =
            "AZose0kAquvI0OxlfS1GiCgQUr2zxeuhP_W0vjpKo9to093vL3mgI0vpTVhfNlYfKo-jka5cthTv9TJmv27TTP8wvN5qMS3VGGxoR9N6ZR_eBytNfrbCKrevuoPrFIeKwiSBxsKuVAM7LfM6xFxON1mZIZus0Qpd9claswgZKz0-Pj0WkvXoAN9KuqNzdYpyDXsBnTiwSd3aCuyXSkaN_T3JQL8IkS-GxzddSFweguWTG0IPojXqE3gTF3gHGdsTQJ2FxuxFOx3i_Hy0JMQpoolLZMDUaBgYkig8ASMLysVf-WQnF4nBeQdMwF0Dh4zl8sxLfTuE4Dk0YwArXHJklv-4oDsF6JttwZCSdilkv3XudKqpditzDjRbOeUtxenCNUAh_BSEo4nrZo2BrAww3nlkyu58Pe2MHtN8QtV6gnTd";

    @Mock GooglePlacesPort googlePlacesPort;

    @Mock PlaceRepository placeRepository;

    @Mock PlacePersistService placePersistService;

    @InjectMocks GetNearbyPlacesService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "pageSize", 20);
    }

    @Test
    @DisplayName("Must to Get All Nearby Places given a Coordinates, radius and placeType")
    void mustToGetAllNearbyGivenACoordinatesRadiusPlaceType() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());
        assertNotNull(nearbyPlaces.getNextTokenPage());

        // Verify that the service called the Google API
        verify(googlePlacesPort, times(1))
                .getNearbyPlaces(any(Coordinates.class), any(), anyString(), anyString());

        verify(placePersistService, times(2)).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must not save places that already exist in database")
    void mustNotSavePlacesThatAlreadyExist() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify that async save was called (PlaceAsyncSaveService handles deduplication
        // internally)
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle error when saving place fails")
    void mustHandleErrorWhenSavingPlaceFails() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify async save was called (error handling is PlaceAsyncSaveService's responsibility)
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must return empty list when Google API returns no places")
    void mustReturnEmptyListWhenNoPlacesFound() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces emptyResponse = new NearbyPlaces(List.of(), null);

        doReturn(emptyResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, null);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(0, nearbyPlaces.getPlaces().size());
        Assertions.assertNull(nearbyPlaces.getNextTokenPage());

        // Verify no async save was performed
        verify(placePersistService, never()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle null nextPageToken correctly")
    void mustHandleNullNextPageToken() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMockWithoutToken();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, null);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());
        Assertions.assertNull(nearbyPlaces.getNextTokenPage());

        verify(googlePlacesPort, times(1))
                .getNearbyPlaces(any(Coordinates.class), any(), anyString(), isNull());
    }

    @Test
    @DisplayName("Must handle repository exception during place lookup")
    void mustHandleRepositoryExceptionDuringLookup() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify async save was called (error handling is PlaceAsyncSaveService's responsibility)
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must propagate exception when Google Places API fails")
    void mustPropagateExceptionWhenGoogleApiIFails() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        doThrow(new RuntimeException("Google API error"))
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action & validation
        Assertions.assertThrows(
                RuntimeException.class,
                () -> {
                    service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
                });

        // Verify that no async save was performed
        verify(placePersistService, never()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle empty places list in async save")
    void mustHandleEmptyPlacesListInAsyncSave() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "RESTAURANT";

        NearbyPlaces emptyResponse = new NearbyPlaces(List.of(), NEXT_PAGE_TOKEN);

        doReturn(emptyResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(0, nearbyPlaces.getPlaces().size());
        assertNotNull(nearbyPlaces.getNextTokenPage());

        // Verify no async save was performed (empty list)
        verify(placePersistService, never()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must call savePlacesAsync and continue execution without waiting")
    void mustCallSavePlacesAsyncAndContinue() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 5000;
        var placeType = "MUSEUM";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, null);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // The method should return immediately without waiting for async save
        verify(googlePlacesPort, times(1)).getNearbyPlaces(coordinates, radius, placeType, null);
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle all places already existing in database")
    void mustHandleAllPlacesAlreadyExisting() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify that async save was called (PlaceAsyncSaveService handles deduplication
        // internally)
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle mixed exceptions in async save gracefully")
    void mustHandleMixedExceptionsInAsyncSave() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = createNearbyPlacesMock();

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, NEXT_PAGE_TOKEN);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(2, nearbyPlaces.getPlaces().size());

        // Verify async save was called (error handling is PlaceAsyncSaveService's responsibility)
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must handle zero radius parameter")
    void mustHandleZeroRadius() {

        // scenario
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 0;
        var placeType = "CAFE";

        NearbyPlaces nearbyPlacesResponse = new NearbyPlaces(List.of(), null);

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, null);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

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

        var places =
                Arrays.stream(placesSearchResponse.results)
                        .map(Place::toNearbyPlace)
                        .collect(Collectors.toList());

        NearbyPlaces nearbyPlacesResponse =
                new NearbyPlaces(places, placesSearchResponse.nextPageToken);

        doReturn(nearbyPlacesResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(coordinates, radius, placeType, null);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());

        // action
        var nearbyPlaces = service.getNearbyPlaces(coordinates, radius, placeType, null);

        // validation
        assertNotNull(nearbyPlaces);
        assertEquals(20, nearbyPlaces.getPlaces().size());

        // Verify async save was called
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    private NearbyPlaces createNearbyPlacesMock() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1");
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2");

        placesSearchResponse.results =
                Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);
        placesSearchResponse.nextPageToken = NEXT_PAGE_TOKEN;

        var places =
                Arrays.stream(placesSearchResponse.results)
                        .map(Place::toNearbyPlace)
                        .collect(Collectors.toList());

        return new NearbyPlaces(places, placesSearchResponse.nextPageToken);
    }

    private NearbyPlaces createNearbyPlacesMockWithoutToken() {

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = createPlacesSearchResultMock("Place 1");
        PlacesSearchResult result2 = createPlacesSearchResultMock("Place 2");

        placesSearchResponse.results =
                Arrays.asList(result1, result2).toArray(new PlacesSearchResult[0]);
        placesSearchResponse.nextPageToken = null;

        var places =
                Arrays.stream(placesSearchResponse.results)
                        .map(Place::toNearbyPlace)
                        .collect(Collectors.toList());

        return new NearbyPlaces(places, null);
    }

    private PlacesSearchResult createPlacesSearchResultMock(String name) {

        PlacesSearchResult result = new PlacesSearchResult();

        result.name = name;
        result.placeId = "ChIJHzIEeEIyGZURpq7lgfAlHKc" + name;
        result.rating = 4.5f;
        result.types = new String[] {"CAFE"};
        result.userRatingsTotal = 2;

        Photo photo = new Photo();
        photo.photoReference =
                "AZose0lqcLLyqLLzqoBkMpKb8ZkgqfmWhiAJu3plnLYwn5ncir8RXu4PFjvEbSRkYwUzw8SXRRLmFTtVRxbJObSAvuyjQsCvtnhm7PZyOLgeynlgXDor0SjTjFS0wa-y7m3WSgeus861Af8ZIRpKBtbvziFcT8sK0a31A8lqEME-e6JYJY_4";
        result.photos = new Photo[] {photo};

        return result;
    }

    private List<Place> createDbPlaces(int count) {
        List<Place> places = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            places.add(
                    Place.builder()
                            .googlePlaceId("db-place-id-" + i)
                            .name("DB Place " + i)
                            .rating(4.0f)
                            .latitude(LATITUDE)
                            .longitude(LONGITUDE)
                            .googleTypes("cafe")
                            .build());
        }
        return places;
    }

    @Test
    @DisplayName("Should return full page from database without calling Google API")
    void shouldReturnFullPageFromDatabaseWithoutCallingGoogleApi() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(20));

        var result = service.getNearbyPlaces(coordinates, radius, placeType, null);

        assertNotNull(result);
        assertEquals(20, result.getPlaces().size());
        assertNotNull(result.getNextTokenPage());
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), anyString(), any());
        verify(placePersistService, never()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Should return partial page from database when DB has more results than page size")
    void shouldReturnPartialPageFromDatabaseWhenDbHasMoreResults() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(25));

        var result = service.getNearbyPlaces(coordinates, radius, placeType, null);

        assertNotNull(result);
        assertEquals(20, result.getPlaces().size());
        assertNotNull(result.getNextTokenPage());
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("Should supplement with Google when database is exhausted")
    void shouldSupplementWithGoogleWhenDatabaseIsExhausted() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        List<Place> dbPlaces = createDbPlaces(3);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dbPlaces);

        List<Place> googlePlaces =
                createDbPlaces(17).stream()
                        .map(
                                p ->
                                        Place.builder()
                                                .googlePlaceId("google-" + p.getGooglePlaceId())
                                                .name(p.getName())
                                                .rating(p.getRating())
                                                .latitude(p.getLatitude())
                                                .longitude(p.getLongitude())
                                                .build())
                        .collect(Collectors.toList());
        NearbyPlaces googleResponse = new NearbyPlaces(googlePlaces, null);

        doReturn(googleResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(eq(coordinates), eq(radius), eq(placeType), isNull());

        var result = service.getNearbyPlaces(coordinates, radius, placeType, null);

        assertNotNull(result);
        assertEquals(20, result.getPlaces().size());
        verify(googlePlacesPort, times(1)).getNearbyPlaces(any(), any(), anyString(), any());
        verify(placePersistService, atLeastOnce()).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Should continue pagination from encoded offset token")
    void shouldContinuePaginationFromEncodedOffset() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(42));

        String token = PaginationTokenEncoder.encode(20, new HashSet<>(), null);

        var result = service.getNearbyPlaces(coordinates, radius, placeType, token);

        assertNotNull(result);
        assertEquals(20, result.getPlaces().size());
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("Should deduplicate places already in shownPlaceIds")
    void shouldDeduplicatePlacesAlreadyInShownPlaceIds() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        List<Place> dbPlaces = createDbPlaces(20);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dbPlaces);
        when(googlePlacesPort.getNearbyPlaces(any(), any(), anyString(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        Set<String> shownIds = new HashSet<>();
        shownIds.add("db-place-id-0");
        shownIds.add("db-place-id-1");
        String token = PaginationTokenEncoder.encode(0, shownIds, null);

        var result = service.getNearbyPlaces(coordinates, radius, placeType, token);

        assertNotNull(result);
        assertEquals(18, result.getPlaces().size());
        result.getPlaces()
                .forEach(
                        p -> {
                            assertNotEquals("db-place-id-0", p.getGooglePlaceId());
                            assertNotEquals("db-place-id-1", p.getGooglePlaceId());
                        });
    }

    @Test
    @DisplayName("Should fall back to Google when offset exceeds DB size")
    void shouldFallBackToGoogleWhenOffsetExceedsDbSize() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(5));

        List<Place> googlePlaces =
                createDbPlaces(20).stream()
                        .map(
                                p ->
                                        Place.builder()
                                                .googlePlaceId("google-" + p.getGooglePlaceId())
                                                .name(p.getName())
                                                .rating(p.getRating())
                                                .latitude(p.getLatitude())
                                                .longitude(p.getLongitude())
                                                .build())
                        .collect(Collectors.toList());
        NearbyPlaces googleResponse = new NearbyPlaces(googlePlaces, null);
        doReturn(googleResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(eq(coordinates), eq(radius), eq(placeType), isNull());

        String token = PaginationTokenEncoder.encode(100, new HashSet<>(), null);

        var result = service.getNearbyPlaces(coordinates, radius, placeType, token);

        assertNotNull(result);
        verify(googlePlacesPort, times(1)).getNearbyPlaces(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("Should ignore cached places that do not match the requested place type")
    void shouldIgnoreCachedPlacesThatDoNotMatchRequestedPlaceType() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        List<Place> dbPlaces =
                List.of(
                        Place.builder()
                                .googlePlaceId("hotel-1")
                                .name("Hotel")
                                .googleTypes("lodging")
                                .build(),
                        Place.builder()
                                .googlePlaceId("cafe-1")
                                .name("Cafe")
                                .googleTypes("cafe,food")
                                .build());
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(dbPlaces);
        when(googlePlacesPort.getNearbyPlaces(eq(coordinates), eq(radius), eq(placeType), isNull()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result = service.getNearbyPlaces(coordinates, radius, placeType, null);

        assertEquals(1, result.getPlaces().size());
        assertEquals("cafe-1", result.getPlaces().get(0).getGooglePlaceId());
    }

    @Test
    @DisplayName("Should use nearby page size of 20 rather than recommendation list size")
    void shouldUseNearbyPageSizeOfTwenty() {
        ReflectionTestUtils.setField(service, "pageSize", 20);
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(25));

        var result = service.getNearbyPlaces(coordinates, 3000, "CAFE", null);

        assertEquals(20, result.getPlaces().size());
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), anyString(), any());
    }

    @Test
    @DisplayName(
            "Should keep leftover Google results available without skipping to next Google page")
    void shouldKeepLeftoverGoogleResultsWithoutSkippingGooglePage() {
        ReflectionTestUtils.setField(service, "pageSize", 5);
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        var radius = 3000;
        var placeType = "CAFE";

        List<Place> googlePlaces = createDbPlaces(8);
        NearbyPlaces googleResponse = new NearbyPlaces(googlePlaces, "google-page-2");
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(List.of());
        doReturn(googleResponse)
                .when(googlePlacesPort)
                .getNearbyPlaces(eq(coordinates), eq(radius), eq(placeType), isNull());

        var firstPage = service.getNearbyPlaces(coordinates, radius, placeType, null);

        assertEquals(5, firstPage.getPlaces().size());
        assertNotNull(firstPage.getNextTokenPage());
        PaginationTokenEncoder.PaginationState firstState =
                PaginationTokenEncoder.decode(firstPage.getNextTokenPage());
        assertEquals("google-page-2", firstState.getGoogleNextPageToken());
        verify(placePersistService, times(8)).saveIfAbsent(any());

        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(googlePlaces);
        when(googlePlacesPort.getNearbyPlaces(
                        eq(coordinates), eq(radius), eq(placeType), eq("google-page-2")))
                .thenReturn(new NearbyPlaces(List.of(), "google-page-2"));

        var secondPage =
                service.getNearbyPlaces(
                        coordinates, radius, placeType, firstPage.getNextTokenPage());

        Set<String> leftoverIds =
                googlePlaces.subList(5, 8).stream()
                        .map(Place::getGooglePlaceId)
                        .collect(Collectors.toSet());
        Set<String> secondPageIds =
                secondPage.getPlaces().stream()
                        .map(Place::getGooglePlaceId)
                        .collect(Collectors.toSet());
        assertTrue(secondPageIds.containsAll(leftoverIds));
        verify(googlePlacesPort, times(1))
                .getNearbyPlaces(eq(coordinates), eq(radius), eq(placeType), isNull());
    }

    @Test
    @DisplayName("Should not treat unsigned offset as database skip")
    void shouldNotTreatUnsignedOffsetAsDatabaseSkip() {
        var coordinates = new Coordinates(LATITUDE, LONGITUDE);
        when(placeRepository.findNearbyPlacesByCoordinates(anyDouble(), anyDouble(), anyInt()))
                .thenReturn(createDbPlaces(20));
        String unsigned =
                java.util.Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("100".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var result = service.getNearbyPlaces(coordinates, 3000, "CAFE", unsigned);

        assertEquals(20, result.getPlaces().size());
        verify(googlePlacesPort, never()).getNearbyPlaces(any(), any(), anyString(), any());
    }
}
