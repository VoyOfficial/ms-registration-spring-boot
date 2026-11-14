package br.voy.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlacePrincipalPhotosTest {

    @Test
    @DisplayName("Principal photos skip base64 payloads above the list ceiling")
    void principalPhotosSkipBase64PayloadsAboveTheListCeiling() {
        String oversized = "a".repeat(Place.MAX_LIST_PHOTO_BASE64_CHARS + 1);
        String allowed = "b".repeat(Place.MAX_LIST_PHOTO_BASE64_CHARS);

        Place place =
                Place.builder()
                        .principalPhoto("ref-big")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("ref-big")
                                                .imageBase64(oversized)
                                                .build(),
                                        PlacePhoto.builder()
                                                .photoReference("ref-ok")
                                                .imageBase64(allowed)
                                                .build()))
                        .build();

        assertThat(place.principalPhotos()).hasSize(1);
        assertThat(place.principalPhotos().get(0).getPhotoReference()).isEqualTo("ref-ok");
        assertThat(place.principalPhotoBase64()).isEqualTo(allowed);
    }

    @Test
    @DisplayName("Principal photos keep a payload at the list ceiling")
    void principalPhotosKeepAPayloadAtTheListCeiling() {
        String allowed = "c".repeat(Place.MAX_LIST_PHOTO_BASE64_CHARS);

        Place place =
                Place.builder()
                        .principalPhoto("ref-ok")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("ref-ok")
                                                .imageBase64(allowed)
                                                .build()))
                        .build();

        assertThat(place.principalPhotoBase64()).isEqualTo(allowed);
    }
}
