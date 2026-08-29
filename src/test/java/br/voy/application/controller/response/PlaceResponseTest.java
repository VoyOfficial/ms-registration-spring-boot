package br.voy.application.controller.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceResponseTest {

    @Test
    @DisplayName("Should not expose Google API key in photo URL")
    void shouldNotExposeGoogleApiKeyInPhotoUrl() {
        Place place =
                Place.builder()
                        .googlePlaceId("place-1")
                        .name("Cafe")
                        .principalPhoto("photo-ref")
                        .principalPhotoUrl(
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=photo-ref&key=secret-key")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("photo-ref")
                                                .photoUrl(
                                                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=photo-ref&key=secret-key")
                                                .build()))
                        .build();

        PlaceResponse response = PlaceResponse.fromDomain(place);

        assertEquals("photo-ref", response.getPhotoReference());
        assertNull(response.getPhoto());
        assertFalse(String.valueOf(response).contains("secret-key"));
        assertNull(response.getPhotos().get(0).getPhotoUrl());
    }
}
