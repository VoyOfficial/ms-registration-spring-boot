package br.voy.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.maps.model.Photo;
import com.google.maps.model.PlacesSearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceNearbyMappingTest {

    @Test
    @DisplayName("Should map nearby place without embedding API key in photo URL")
    void shouldMapNearbyPlaceWithoutEmbeddingApiKey() {
        PlacesSearchResult result = new PlacesSearchResult();
        result.placeId = "google-id";
        result.name = "Cafe";
        result.types = new String[] {"cafe", "food"};
        Photo photo = new Photo();
        photo.photoReference = "ref-123";
        result.photos = new Photo[] {photo};

        Place place = Place.toNearbyPlace(result, "super-secret-key");

        assertEquals("ref-123", place.getPrincipalPhoto());
        assertNull(place.getPrincipalPhotoUrl());
        assertEquals("cafe,food", place.getGoogleTypes());
        assertFalse(String.valueOf(place).contains("super-secret-key"));
    }
}
