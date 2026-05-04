package br.voy.domain.service;

import br.voy.domain.entity.Place;
import br.voy.domain.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceAsyncSaveServiceTest {

    @Mock
    PlaceRepository placeRepository;

    @InjectMocks
    PlaceAsyncSaveService placeAsyncSaveService;

    @Test
    @DisplayName("Should save new place when not existing in repository")
    void shouldSaveNewPlaceWhenNotExistingInRepository() {
        Place place = buildPlace("google-id-1", "Place One", "11 99999-0001", 4.5f, 1.5f);
        when(placeRepository.findPlaceByGooglePlaceId("google-id-1")).thenReturn(Optional.empty());

        placeAsyncSaveService.savePlacesAsync(List.of(place));

        verify(placeRepository, times(1)).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Should set default contact when contact is null")
    void shouldSetDefaultContactWhenContactIsNull() {
        Place place = buildPlace("google-id-2", "Place Two", null, 4.0f, 2.0f);
        when(placeRepository.findPlaceByGooglePlaceId("google-id-2")).thenReturn(Optional.empty());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        placeAsyncSaveService.savePlacesAsync(List.of(place));

        verify(placeRepository).savePlace(captor.capture());
        assertEquals("", captor.getValue().getContact());
    }

    @Test
    @DisplayName("Should set default distanceOfLocal when distanceOfLocal is null")
    void shouldSetDefaultDistanceWhenDistanceIsNull() {
        Place place = buildPlace("google-id-3", "Place Three", null, 3.5f, null);
        when(placeRepository.findPlaceByGooglePlaceId("google-id-3")).thenReturn(Optional.empty());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        placeAsyncSaveService.savePlacesAsync(List.of(place));

        verify(placeRepository).savePlace(captor.capture());
        assertEquals(0.0f, captor.getValue().getDistanceOfLocal());
    }

    @Test
    @DisplayName("Should set default rating when rating is null")
    void shouldSetDefaultRatingWhenRatingIsNull() {
        Place place = buildPlace("google-id-4", "Place Four", null, null, null);
        when(placeRepository.findPlaceByGooglePlaceId("google-id-4")).thenReturn(Optional.empty());

        ArgumentCaptor<Place> captor = ArgumentCaptor.forClass(Place.class);
        placeAsyncSaveService.savePlacesAsync(List.of(place));

        verify(placeRepository).savePlace(captor.capture());
        assertEquals(0.0f, captor.getValue().getRating());
    }

    @Test
    @DisplayName("Should skip place when already exists in repository")
    void shouldSkipPlaceWhenAlreadyExists() {
        Place existing = buildPlace("google-id-5", "Existing Place", "11 99999-0005", 4.0f, 1.0f);
        when(placeRepository.findPlaceByGooglePlaceId("google-id-5")).thenReturn(Optional.of(existing));

        placeAsyncSaveService.savePlacesAsync(List.of(existing));

        verify(placeRepository, never()).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Should continue saving remaining places when one place throws unexpected exception")
    void shouldContinueSavingWhenOnePlaceThrowsException() {
        Place place1 = buildPlace("google-id-6", "Place Six", "11 99999-0006", 4.0f, 1.0f);
        Place place2 = buildPlace("google-id-7", "Place Seven", "11 99999-0007", 4.0f, 1.0f);

        when(placeRepository.findPlaceByGooglePlaceId("google-id-6")).thenReturn(Optional.empty());
        when(placeRepository.findPlaceByGooglePlaceId("google-id-7")).thenThrow(new RuntimeException("DB error"));

        placeAsyncSaveService.savePlacesAsync(List.of(place1, place2));

        verify(placeRepository, times(1)).savePlace(any(Place.class));
    }

    private Place buildPlace(String googleId, String name, String contact, Float rating, Float distance) {
        return Place.builder()
                .googlePlaceId(googleId)
                .name(name)
                .contact(contact)
                .rating(rating)
                .distanceOfLocal(distance)
                .build();
    }
}
