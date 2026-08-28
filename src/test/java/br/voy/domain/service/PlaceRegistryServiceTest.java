package br.voy.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.exception.CityDifferentPlaceRecommendationException;
import br.voy.domain.exception.PlaceAlreadyExistsException;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlaceRegistryServiceTest {

    @Mock private PlaceRepository repository;

    @Mock private GooglePlacesPort googlePlacesPort;

    @InjectMocks private PlaceRegistryService placeRegistryService;

    @Test
    @DisplayName("Must to Registry an Recommended Place")
    void mustToRegistryAnRecommendPlace() {

        // scenario
        var expectedRecommendedPlaceId = 1L;
        Place placeDomain =
                Place.builder()
                        .id(expectedRecommendedPlaceId)
                        .name("Test Recommended Place")
                        .city("Gramado")
                        .ranking(2)
                        .build();

        PlaceDetails recommendedPlaceDetails = createRecommendedPlaceDetails();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(recommendedPlaceDetails);
        when(repository.findPlaceByGooglePlaceId(recommendedPlaceDetails.getGooglePlaceId()))
                .thenReturn(Optional.empty());

        when(repository.savePlace(any(Place.class))).thenReturn(placeDomain);

        // action
        Long recommendedPlaceId = placeRegistryService.registry(placeDomain);

        // validation
        assertEquals(expectedRecommendedPlaceId, recommendedPlaceId);

        verify(repository, times(1)).findPlaceByGooglePlaceId(anyString());
        verify(repository, times(1)).savePlace(any(Place.class));
    }

    @Test
    @DisplayName(
            "Don't should to Registry an Recommended Place when this already exists in database")
    void dontShouldToRegistryAnRecommendedPlaceWhenThisAlreadyExistsInDatabase() {

        // scenario
        var expectedExceptionMessage = "place.already.exists.default.message";
        var expectedRecommendedPlaceId = 1L;
        Place placeDomain =
                Place.builder()
                        .id(expectedRecommendedPlaceId)
                        .name("Test Recommended Place")
                        .city("Gramado")
                        .latitude(-23.55)
                        .longitude(-46.66)
                        .ranking(2)
                        .build();

        PlaceDetails recommendedPlaceDetails = createRecommendedPlaceDetails();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(recommendedPlaceDetails);
        when(repository.findPlaceByGooglePlaceId(recommendedPlaceDetails.getGooglePlaceId()))
                .thenReturn(Optional.of(placeDomain));

        // action
        var raisedException =
                assertThrows(
                        PlaceAlreadyExistsException.class,
                        () -> placeRegistryService.registry(placeDomain));

        // validation
        assertEquals(PlaceAlreadyExistsException.class, raisedException.getClass());
        assertEquals(expectedExceptionMessage, raisedException.getMessage());
    }

    @Test
    @DisplayName(
            "Don't should to Registry an Recommended Place when this city is different between city received in PlaceDomain")
    void
            dontShouldToRegistryAnRecommendedPlaceWhenThisCityIsDifferentBetweenCityReceivedInPlaceDomain() {

        // scenario
        var expectedExceptionMessage = "place.with.city.different.google.default.message";
        var expectedRecommendedPlaceId = 1L;
        Place placeDomain =
                Place.builder()
                        .id(expectedRecommendedPlaceId)
                        .name("Test Recommended Place")
                        .city("Test City")
                        .ranking(2)
                        .build();

        PlaceDetails recommendedPlaceDetails = createRecommendedPlaceDetails();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(recommendedPlaceDetails);

        // action
        var raisedException =
                assertThrows(
                        CityDifferentPlaceRecommendationException.class,
                        () -> placeRegistryService.registry(placeDomain));

        // validation
        assertEquals(CityDifferentPlaceRecommendationException.class, raisedException.getClass());
        assertEquals(expectedExceptionMessage, raisedException.getMessage());
    }

    private static PlaceDetails createRecommendedPlaceDetails() {

        return PlaceDetails.builder()
                .googlePlaceId("ChIJPQmNhEMyGZURxuHk44vIaIw")
                .name("Test Recommended Place")
                .contact("(54) 3286-4040")
                .latitude(-23.55)
                .longitude(-46.66)
                .address("R. Wilma Dinnebier - Bairro Belverede, Gramado - RS, 95670-192, Brazil")
                .build();
    }

    @Test
    @DisplayName("Should set status true when registering a place")
    void shouldSetStatusTrueWhenRegisteringPlace() {
        Place placeDomain =
                Place.builder()
                        .id(1L)
                        .name("Test Recommended Place")
                        .city("Gramado")
                        .ranking(2)
                        .build();
        PlaceDetails placeDetails = createRecommendedPlaceDetails();
        Place savedPlace = Place.builder().id(1L).build();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(placeDetails);
        when(repository.findPlaceByGooglePlaceId(placeDetails.getGooglePlaceId()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        when(repository.savePlace(captor.capture())).thenReturn(savedPlace);

        placeRegistryService.registry(placeDomain);

        assertTrue(captor.getValue().isStatus(), "esperado que status seja true");
    }

    @Test
    @DisplayName("Should set recommendation dates when registering a place")
    void shouldSetRecommendationDatesWhenRegisteringPlace() {
        LocalDate today = LocalDate.now();
        Place placeDomain =
                Place.builder()
                        .id(1L)
                        .name("Test Recommended Place")
                        .city("Gramado")
                        .ranking(2)
                        .build();
        PlaceDetails placeDetails = createRecommendedPlaceDetails();
        Place savedPlace = Place.builder().id(1L).build();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(placeDetails);
        when(repository.findPlaceByGooglePlaceId(placeDetails.getGooglePlaceId()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        when(repository.savePlace(captor.capture())).thenReturn(savedPlace);

        placeRegistryService.registry(placeDomain);

        Place captured = captor.getValue();
        assertEquals(
                today,
                captured.getStartRecommendation(),
                "esperado que startRecommendation seja hoje");
        assertEquals(today, captured.getCreatedAt(), "esperado que createdAt seja hoje");
        assertEquals(
                today.plusMonths(1),
                captured.getEndRecommendation(),
                "esperado que endRecommendation seja hoje + 1 mês");
    }

    @Test
    @DisplayName(
            "Should extract city from fallback address segment when primary segment does not match")
    void shouldExtractCityFromFallbackAddressSegmentWhenPrimarySegmentDoesNotMatch() {
        Place placeDomain =
                Place.builder().id(1L).name("Test Place").city("Gramado").ranking(1).build();
        PlaceDetails placeDetailsWithFallbackAddress =
                PlaceDetails.builder()
                        .googlePlaceId("ChIJPQmNhEMyGZURxuHk44vIaIw")
                        .name("Test Place")
                        .contact("(54) 3286-4040")
                        .latitude(-29.35)
                        .longitude(-50.87)
                        .address("R. Teste, Bairro Belverede - RS, Gramado - RS, 95670-000, Brazil")
                        .build();
        Place savedPlace = Place.builder().id(1L).build();

        when(googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity()))
                .thenReturn(placeDetailsWithFallbackAddress);
        when(repository.findPlaceByGooglePlaceId(
                        placeDetailsWithFallbackAddress.getGooglePlaceId()))
                .thenReturn(Optional.empty());
        when(repository.savePlace(any(Place.class))).thenReturn(savedPlace);

        Long id = placeRegistryService.registry(placeDomain);

        assertEquals(1L, id);
        verify(repository, times(1)).savePlace(any(Place.class));
    }
}
