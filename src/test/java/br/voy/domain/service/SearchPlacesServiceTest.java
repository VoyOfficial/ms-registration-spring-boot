package br.voy.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.utils.PlaceSearchOrdination;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SearchPlacesServiceTest {

    private static final double LATITUDE = -29.366054;
    private static final double LONGITUDE = -50.877113;

    @Mock GooglePlacesPort googlePlacesPort;

    @Mock PlaceRepository placeRepository;

    @Mock PlacePersistService placePersistService;

    @InjectMocks SearchPlacesService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "pageSize", 20);
        ReflectionTestUtils.setField(service, "earthRadiusKm", 6371.0);
    }

    @Test
    @DisplayName("Must return DB places matching name case-insensitive")
    void mustReturnDbPlacesMatchingNameCaseInsensitive() {
        when(placeRepository.findPlacesByNameContaining("hard"))
                .thenReturn(
                        List.of(
                                place(
                                        "gp1",
                                        "Hard Rock Cafe",
                                        "restaurant",
                                        4.5f,
                                        100,
                                        -29.37,
                                        -50.88),
                                place("gp2", "HARD Rock Bar", "bar", 4.0f, 50, -29.38, -50.89)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces(
                        "hard", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertEquals(2, result.getPlaces().size());
        assertTrue(
                result.getPlaces().stream()
                        .map(Place::getName)
                        .collect(Collectors.toSet())
                        .containsAll(Set.of("Hard Rock Cafe", "HARD Rock Bar")));
        assertNotNull(result.getPlaces().get(0).getDistanceFromUserLocation());
        verify(googlePlacesPort, times(1))
                .textSearch(eq("hard"), any(), anyInt(), isNull(), isNull());
    }

    @Test
    @DisplayName("Must filter by Portuguese type labels mapped to google_types")
    void mustFilterByPortugueseTypeLabels() {
        when(placeRepository.findPlacesByNameContaining("cafe"))
                .thenReturn(
                        List.of(
                                place("gp1", "Cafe A", "restaurant", 4.5f, 10, -29.37, -50.88),
                                place("gp2", "Cafe B", "cafe", 4.0f, 20, -29.38, -50.89),
                                place("gp3", "Cafe C", "lodging", 3.5f, 5, -29.39, -50.90)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces(
                        "cafe",
                        new Coordinates(LATITUDE, LONGITUDE),
                        List.of("Cafeteiras"),
                        null,
                        null);

        assertEquals(1, result.getPlaces().size());
        assertEquals("Cafe B", result.getPlaces().get(0).getName());
    }

    @Test
    @DisplayName("Must not filter by category when types absent or empty")
    void mustNotFilterWhenTypesAbsentOrEmpty() {
        when(placeRepository.findPlacesByNameContaining("local"))
                .thenReturn(
                        List.of(
                                place("gp1", "Local A", "restaurant", 4.5f, 10, -29.37, -50.88),
                                place("gp2", "Local B", "park", 4.0f, 20, -29.38, -50.89)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var withoutTypes =
                service.searchPlaces(
                        "local", new Coordinates(LATITUDE, LONGITUDE), null, null, null);
        var emptyTypes =
                service.searchPlaces(
                        "local", new Coordinates(LATITUDE, LONGITUDE), List.of(), null, null);

        assertEquals(2, withoutTypes.getPlaces().size());
        assertEquals(2, emptyTypes.getPlaces().size());
    }

    @Test
    @DisplayName("Must order by Mais avaliados (rating desc)")
    void mustOrderByMostRated() {
        when(placeRepository.findPlacesByNameContaining("spot"))
                .thenReturn(
                        List.of(
                                place("gp1", "Low", "restaurant", 3.0f, 200, -29.37, -50.88),
                                place("gp2", "High", "restaurant", 4.9f, 10, -29.38, -50.89),
                                place("gp3", "Mid", "restaurant", 4.0f, 50, -29.39, -50.90)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces(
                        "spot",
                        new Coordinates(LATITUDE, LONGITUDE),
                        null,
                        PlaceSearchOrdination.MOST_RATED,
                        null);

        assertEquals(
                List.of("High", "Mid", "Low"),
                result.getPlaces().stream().map(Place::getName).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Must order by Mais comentados (userRatingsTotal desc)")
    void mustOrderByMostCommented() {
        when(placeRepository.findPlacesByNameContaining("spot"))
                .thenReturn(
                        List.of(
                                place("gp1", "Few", "restaurant", 5.0f, 5, -29.37, -50.88),
                                place("gp2", "Many", "restaurant", 3.0f, 500, -29.38, -50.89),
                                place("gp3", "Some", "restaurant", 4.0f, 50, -29.39, -50.90)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces(
                        "spot",
                        new Coordinates(LATITUDE, LONGITUDE),
                        null,
                        PlaceSearchOrdination.MOST_COMMENTED,
                        null);

        assertEquals(
                List.of("Many", "Some", "Few"),
                result.getPlaces().stream().map(Place::getName).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Must order by closest by default when ordination absent")
    void mustOrderByClosestByDefault() {
        when(placeRepository.findPlacesByNameContaining("spot"))
                .thenReturn(
                        List.of(
                                place("gp1", "Far", "restaurant", 5.0f, 10, -29.50, -51.00),
                                place("gp2", "Near", "restaurant", 3.0f, 10, -29.3661, -50.8772),
                                place("gp3", "Mid", "restaurant", 4.0f, 10, -29.40, -50.90)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces(
                        "spot", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertEquals("Near", result.getPlaces().get(0).getName());
        assertEquals("Far", result.getPlaces().get(result.getPlaces().size() - 1).getName());
    }

    @Test
    @DisplayName("Must supplement from Google Text Search without duplicating googlePlaceId")
    void mustSupplementFromGoogleTextSearchWithoutDuplicates() {
        ReflectionTestUtils.setField(service, "pageSize", 3);
        when(placeRepository.findPlacesByNameContaining("rock"))
                .thenReturn(
                        List.of(place("gp1", "Rock DB", "restaurant", 4.0f, 10, -29.37, -50.88)));

        List<Place> googlePlaces =
                List.of(
                        place("gp1", "Rock DB", "restaurant", 4.0f, 10, -29.37, -50.88),
                        place("gp2", "Rock Google A", "restaurant", 4.5f, 20, -29.38, -50.89),
                        place("gp3", "Rock Google B", "restaurant", 4.2f, 15, -29.39, -50.90));
        doReturn(new NearbyPlaces(googlePlaces, null))
                .when(googlePlacesPort)
                .textSearch(eq("rock"), any(), anyInt(), isNull(), isNull());

        var result =
                service.searchPlaces(
                        "rock", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertEquals(3, result.getPlaces().size());
        Set<String> ids =
                result.getPlaces().stream()
                        .map(Place::getGooglePlaceId)
                        .collect(Collectors.toSet());
        assertEquals(3, ids.size());
        assertTrue(ids.contains("gp1"));
        assertTrue(ids.contains("gp2"));
        assertTrue(ids.contains("gp3"));
        verify(placePersistService, times(3)).saveIfAbsent(any());
    }

    @Test
    @DisplayName("Must paginate across pages with HMAC nextPageToken")
    void mustPaginateAcrossPagesWithToken() {
        ReflectionTestUtils.setField(service, "pageSize", 2);
        List<Place> dbPlaces =
                List.of(
                        place("gp1", "A Spot", "restaurant", 4.0f, 10, -29.3661, -50.8772),
                        place("gp2", "B Spot", "restaurant", 4.0f, 10, -29.3662, -50.8773),
                        place("gp3", "C Spot", "restaurant", 4.0f, 10, -29.3663, -50.8774),
                        place("gp4", "D Spot", "restaurant", 4.0f, 10, -29.3664, -50.8775));
        when(placeRepository.findPlacesByNameContaining("Spot")).thenReturn(dbPlaces);

        var firstPage =
                service.searchPlaces(
                        "Spot", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertEquals(2, firstPage.getPlaces().size());
        assertNotNull(firstPage.getNextTokenPage());

        var secondPage =
                service.searchPlaces(
                        "Spot",
                        new Coordinates(LATITUDE, LONGITUDE),
                        null,
                        null,
                        firstPage.getNextTokenPage());

        assertEquals(2, secondPage.getPlaces().size());
        Set<String> firstIds =
                firstPage.getPlaces().stream()
                        .map(Place::getGooglePlaceId)
                        .collect(Collectors.toCollection(HashSet::new));
        Set<String> secondIds =
                secondPage.getPlaces().stream()
                        .map(Place::getGooglePlaceId)
                        .collect(Collectors.toSet());
        firstIds.retainAll(secondIds);
        assertTrue(firstIds.isEmpty());
        verify(googlePlacesPort, never()).textSearch(anyString(), any(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("Must treat invalid token as first page")
    void mustTreatInvalidTokenAsFirstPage() {
        when(placeRepository.findPlacesByNameContaining("x"))
                .thenReturn(
                        List.of(place("gp1", "X Place", "restaurant", 4.0f, 10, -29.37, -50.88)));
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        String unsigned =
                java.util.Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString("100".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        var result =
                service.searchPlaces(
                        "x", new Coordinates(LATITUDE, LONGITUDE), null, null, unsigned);

        assertEquals(1, result.getPlaces().size());
        assertEquals("gp1", result.getPlaces().get(0).getGooglePlaceId());
    }

    @Test
    @DisplayName("Must skip Google when DB already fills the page")
    void mustSkipGoogleWhenDbFillsPage() {
        ReflectionTestUtils.setField(service, "pageSize", 2);
        when(placeRepository.findPlacesByNameContaining("full"))
                .thenReturn(
                        List.of(
                                place("gp1", "Full 1", "restaurant", 4.0f, 10, -29.37, -50.88),
                                place("gp2", "Full 2", "restaurant", 4.0f, 10, -29.38, -50.89),
                                place("gp3", "Full 3", "restaurant", 4.0f, 10, -29.39, -50.90)));

        var result =
                service.searchPlaces(
                        "full", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertEquals(2, result.getPlaces().size());
        assertNotNull(result.getNextTokenPage());
        verify(googlePlacesPort, never()).textSearch(anyString(), any(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("Must return empty when DB and Google have no matches")
    void mustReturnEmptyWhenNoMatches() {
        when(placeRepository.findPlacesByNameContaining("zzz")).thenReturn(List.of());
        when(googlePlacesPort.textSearch(anyString(), any(), anyInt(), any(), any()))
                .thenReturn(new NearbyPlaces(List.of(), null));

        var result =
                service.searchPlaces("zzz", new Coordinates(LATITUDE, LONGITUDE), null, null, null);

        assertTrue(result.getPlaces().isEmpty());
        assertNull(result.getNextTokenPage());
    }

    private static Place place(
            String googlePlaceId,
            String name,
            String googleTypes,
            Float rating,
            Integer userRatingsTotal,
            double latitude,
            double longitude) {
        return Place.builder()
                .googlePlaceId(googlePlaceId)
                .name(name)
                .googleTypes(googleTypes)
                .rating(rating)
                .userRatingsTotal(userRatingsTotal)
                .address(name + " address")
                .city("Gramado")
                .latitude(latitude)
                .longitude(longitude)
                .photos(new ArrayList<>())
                .build();
    }
}
