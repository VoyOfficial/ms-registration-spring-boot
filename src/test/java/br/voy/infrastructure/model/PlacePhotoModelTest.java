package br.voy.infrastructure.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.voy.domain.entity.PlacePhoto;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlacePhotoModelTest {

    @Test
    @DisplayName("Should reencode decoded image bytes as base64")
    void shouldReencodeDecodedImageBytesAsBase64() {
        byte[] rawImage = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        String expected = Base64.getEncoder().encodeToString(rawImage);
        PlacePhotoModel model =
                new PlacePhotoModel(
                        PlacePhoto.builder().photoReference("ref").imageBase64(expected).build());

        assertEquals(expected, model.toDomain().getImageBase64());
    }

    @Test
    @DisplayName("Should keep legacy rows that stored the base64 text as bytes")
    void shouldKeepLegacyBase64TextBytes() {
        String legacy = "iVBORw0KGgo=";
        PlacePhotoModel model = new PlacePhotoModel();
        model.setPhotoReference("ref");
        model.setImageBase64(legacy.getBytes(StandardCharsets.UTF_8));

        assertEquals(legacy, model.toDomain().getImageBase64());
        assertNull(PlacePhotoModel.toBase64Payload(null));
        assertNull(PlacePhotoModel.toBase64Payload(new byte[0]));
    }
}
