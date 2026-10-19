package br.voy.application.controller.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.entity.PlacePhoto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlaceDetailsResponseTest {

    @Test
    @DisplayName("Should not expose Google API key in place details photo URLs")
    void shouldNotExposeGoogleApiKeyInPhotoUrls() {
        PlaceDetails place =
                PlaceDetails.builder()
                        .googlePlaceId("place-1")
                        .name("Cafe")
                        .businessHours(Collections.emptyList())
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("photo-ref")
                                                .photoUrl(
                                                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=photo-ref&key=secret-key")
                                                .build()))
                        .build();

        PlaceDetailsResponse response = PlaceDetailsResponse.toPlaceDetailsResponse(place);

        assertEquals("photo-ref", response.getPhotos().get(0).getPhotoReference());
        assertNull(response.getPhotos().get(0).getPhotoUrl());
        assertFalse(String.valueOf(response).contains("secret-key"));
    }
}
