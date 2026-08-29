package br.voy.domain.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import br.voy.domain.entity.Place;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceAsyncSaveServiceTest {

    @Mock private PlacePersistService persistService;

    @InjectMocks private PlaceAsyncSaveService asyncSaveService;

    @Test
    @DisplayName("Should persist each place")
    void shouldPersistEachPlace() {
        Place place1 = createTestPlace("Place 1");
        Place place2 = createTestPlace("Place 2");

        asyncSaveService.savePlacesAsync(List.of(place1, place2));

        verify(persistService).saveIfAbsent(place1);
        verify(persistService).saveIfAbsent(place2);
    }

    @Test
    @DisplayName("Should continue when one persist fails")
    void shouldContinueWhenOnePersistFails() {
        Place place1 = createTestPlace("Place 1");
        Place place2 = createTestPlace("Place 2");
        doThrow(new RuntimeException("Database error")).when(persistService).saveIfAbsent(place1);

        asyncSaveService.savePlacesAsync(List.of(place1, place2));

        verify(persistService, times(2)).saveIfAbsent(any(Place.class));
    }

    @Test
    @DisplayName("Should skip persist when list is empty")
    void shouldSkipWhenEmpty() {
        asyncSaveService.savePlacesAsync(List.of());

        verify(persistService, never()).saveIfAbsent(any(Place.class));
    }

    private Place createTestPlace(String name) {
        return Place.builder()
                .name(name)
                .googlePlaceId("google123")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .createdAt(LocalDate.now())
                .photos(List.of())
                .build();
    }
}
