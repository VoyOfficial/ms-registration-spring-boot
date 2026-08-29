package br.voy.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.voy.domain.entity.Place;
import br.voy.domain.repository.PlaceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlacePersistServiceTest {

    @Mock private PlaceRepository placeRepository;

    @InjectMocks private PlacePersistService persistService;

    @Test
    @DisplayName("Should save place when it does not exist")
    void shouldSavePlaceWhenNotExists() {
        Place place = createTestPlace("Test Place", null);
        when(placeRepository.findPlaceByGooglePlaceId("google123")).thenReturn(Optional.empty());
        when(placeRepository.savePlace(any(Place.class))).thenReturn(place);

        persistService.saveIfAbsent(place);

        verify(placeRepository).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Should skip place when it already exists")
    void shouldSkipPlaceWhenAlreadyExists() {
        Place existingPlace = createTestPlace("Existing Place", 1L);
        Place newPlace = createTestPlace("New Place", null);

        when(placeRepository.findPlaceByGooglePlaceId("google123"))
                .thenReturn(Optional.of(existingPlace));

        persistService.saveIfAbsent(newPlace);

        verify(placeRepository, never()).savePlace(any(Place.class));
    }

    @Test
    @DisplayName("Should propagate exception when save fails")
    void shouldPropagateExceptionWhenSaveFails() {
        Place place = createTestPlace("Error Place", null);
        when(placeRepository.findPlaceByGooglePlaceId("google123")).thenReturn(Optional.empty());
        when(placeRepository.savePlace(any(Place.class)))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> persistService.saveIfAbsent(place));
    }

    private Place createTestPlace(String name, Long id) {
        return Place.builder()
                .id(id)
                .name(name)
                .googlePlaceId("google123")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .createdAt(LocalDate.now())
                .photos(List.of())
                .build();
    }
}
