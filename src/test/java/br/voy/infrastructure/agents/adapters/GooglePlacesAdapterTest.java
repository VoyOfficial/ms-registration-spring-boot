package br.voy.infrastructure.agents.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.infrastructure.agents.PlacesApiClient;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GooglePlacesAdapterTest {

    @Mock private PlacesApiClient placesApiClient;

    @InjectMocks private GooglePlacesAdapter adapter;

    @Test
    @DisplayName("Should expose the downloaded Google photo as banner base64")
    void shouldExposeDownloadedGooglePhotoAsBannerBase64() {
        Photo photo = new Photo();
        photo.photoReference = "google-ref";

        PlacesSearchResult result = new PlacesSearchResult();
        result.placeId = "google-place";
        result.name = "Google Museum";
        result.geometry = new Geometry();
        result.geometry.location = new LatLng(-29.38, -50.87);
        result.photos = new Photo[] {photo};

        PlacesSearchResponse response = new PlacesSearchResponse();
        response.results = new PlacesSearchResult[] {result};

        when(placesApiClient.searchForNearbyPlaces(any(), any(), any(), any()))
                .thenReturn(response);
        when(placesApiClient.getPlacePhotos(any()))
                .thenReturn(
                        List.of(
                                PlacePhoto.builder()
                                        .photoReference("google-ref")
                                        .imageBase64("R29vZ2xlQmFubmVy")
                                        .build()));

        var nearby = adapter.getNearbyPlaces(new Coordinates(-29.38, -50.87), 5000, "", null);
        Place place = nearby.getPlaces().get(0);

        assertEquals("google-ref", place.getPrincipalPhoto());
        assertEquals("R29vZ2xlQmFubmVy", place.getPhotos().get(0).getImageBase64());
    }
}
