package br.voy.application.controller.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("Should put principal photo base64 in photo for a Google place")
    void shouldExposePrincipalPhotoBase64FromGooglePlace() {
        String banner = "R29vZ2xlQmFubmVy";
        Place place =
                Place.builder()
                        .googlePlaceId("google-1")
                        .name("Museum")
                        .principalPhoto("ref-principal")
                        .principalPhotoUrl(
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=ref-principal&key=secret-key")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("ref-other")
                                                .imageBase64("b3RoZXI=")
                                                .build(),
                                        PlacePhoto.builder()
                                                .photoReference("ref-principal")
                                                .imageBase64(banner)
                                                .photoUrl(
                                                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=ref-principal&key=secret-key")
                                                .build()))
                        .build();

        PlaceResponse response = PlaceResponse.fromDomain(place);

        assertEquals(banner, response.getPhoto());
        assertTrue(response.getPhoto().matches("^[A-Za-z0-9+/=]+$"));
        assertFalse(response.getPhoto().contains("key="));
        assertFalse(String.valueOf(response).contains("secret-key"));
    }

    @Test
    @DisplayName("Should use the first photo base64 when the principal reference misses")
    void shouldFallbackToFirstPhotoBase64() {
        Place place =
                Place.builder()
                        .googlePlaceId("db-1")
                        .name("Park")
                        .principalPhoto("missing-ref")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("stored-ref")
                                                .imageBase64("cGVyc2lzdGVk")
                                                .build()))
                        .build();

        PlaceResponse response = PlaceResponse.fromDomain(place);

        assertEquals("cGVyc2lzdGVk", response.getPhoto());
    }
}
