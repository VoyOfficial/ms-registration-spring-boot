package br.voy.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.voy.application.controller.response.PlaceResponse;
import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.infrastructure.agents.PlacesApiClient;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoJpaRepository;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class GetNearbyPlacesServiceIntegrationTest {

    private static final String GALLERY = Base64.getEncoder().encodeToString(new byte[6000]);
    private static final String GOOGLE_GALLERY = googleGallery();
    private static final String STORED_BANNER =
            Base64.getEncoder().encodeToString(new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 1});
    private static final String GOOGLE_BANNER =
            Base64.getEncoder()
                    .encodeToString(new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 4});

    private static String googleGallery() {
        byte[] image = new byte[6000];
        Arrays.fill(image, (byte) 7);
        return Base64.getEncoder().encodeToString(image);
    }

    @Autowired private GetNearbyPlacesService service;

    @Autowired private PlaceRepository placeRepository;

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @Autowired private PlacePhotoJpaRepository placePhotoJpaRepository;

    @MockBean private GooglePlacesPort googlePlacesPort;

    @MockBean private PlacesApiClient placesApiClient;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        placePhotoJpaRepository.deleteAll();
        placeJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Service list keeps the principal photo when the gallery is large")
    void serviceListKeepsPrincipalPhotoWhenGalleryIsLarge() {
        placeRepository.savePlace(
                Place.builder()
                        .googlePlaceId("svc-db-1")
                        .name("Stored")
                        .address("Rua 1")
                        .city("Canela")
                        .latitude(-29.381)
                        .longitude(-50.871)
                        .principalPhoto("banner-ref")
                        .googleTypes("park")
                        .photos(
                                List.of(
                                        PlacePhoto.builder()
                                                .photoReference("gallery-ref")
                                                .imageBase64(GALLERY)
                                                .build(),
                                        PlacePhoto.builder()
                                                .photoReference("banner-ref")
                                                .imageBase64(STORED_BANNER)
                                                .build()))
                        .build());

        when(googlePlacesPort.getNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(
                        new NearbyPlaces(
                                List.of(
                                        Place.builder()
                                                .googlePlaceId("svc-google-1")
                                                .name("From Google")
                                                .latitude(-29.382)
                                                .longitude(-50.872)
                                                .principalPhoto("g-banner")
                                                .photos(
                                                        List.of(
                                                                PlacePhoto.builder()
                                                                        .photoReference("g-other")
                                                                        .imageBase64(GOOGLE_GALLERY)
                                                                        .build(),
                                                                PlacePhoto.builder()
                                                                        .photoReference("g-banner")
                                                                        .imageBase64(GOOGLE_BANNER)
                                                                        .build()))
                                                .build()),
                                null));

        var result = service.getNearbyPlaces(new Coordinates(-29.38, -50.87), 5000, "", "");

        assertThat(result.getPlaces()).hasSize(2);
        assertThat(placeNamed(result, "Stored").getPhotos())
                .extracting(PlacePhoto::getImageBase64)
                .containsExactly(STORED_BANNER);
        assertThat(placeNamed(result, "From Google").getPhotos())
                .extracting(PlacePhoto::getImageBase64)
                .containsExactly(GOOGLE_BANNER);

        PlaceResponse stored = PlaceResponse.fromDomain(placeNamed(result, "Stored"));
        PlaceResponse google = PlaceResponse.fromDomain(placeNamed(result, "From Google"));
        assertThat(stored.getPhoto()).isEqualTo(STORED_BANNER);
        assertThat(google.getPhoto()).isEqualTo(GOOGLE_BANNER);
        assertThat(stored.getPhotos()).isNull();
        assertThat(google.getPhotos()).isNull();
        assertThat(stored.toString()).doesNotContain(GALLERY);
        assertThat(google.toString()).doesNotContain(GOOGLE_GALLERY);
        assertThat(stored.getPhoto().length()).isLessThan(GALLERY.length());
        assertThat(google.getPhoto().length()).isLessThan(GOOGLE_GALLERY.length());

        var persistedGoogle = placeJpaRepository.findByGooglePlaceId("svc-google-1");
        assertThat(persistedGoogle).isPresent();
        assertThat(placePhotoJpaRepository.findByPlaceId(persistedGoogle.get().getId())).hasSize(2);
    }

    private static Place placeNamed(NearbyPlaces result, String name) {
        return result.getPlaces().stream()
                .filter(place -> name.equals(place.getName()))
                .findFirst()
                .orElseThrow();
    }
}
