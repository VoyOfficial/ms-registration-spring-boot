package src.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import src.domain.entity.Place;
import src.domain.ports.GooglePlacesPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GetPlaceDetailsServiceTest {

    @Mock
    GooglePlacesPort googlePlacesPort;

    @InjectMocks
    GetPlaceDetailsService service;

    @Test
    @DisplayName("Must to Get Place Details given a placeId")
    void mustToGetPlaceDetailsGivenAPlaceId() {

        // scenario
        var placeId = "ChIJQXQEc04yGZURHsEanxBefFw";

        var expectedPlaceDetails = createPlaceDetails(placeId);
        doReturn(expectedPlaceDetails).when(googlePlacesPort).getPlaceDetails(placeId);

        // action
        Place placeDetails = service.getPlaceDetails(placeId);

        // validation
        assertNotNull(placeDetails);
        assertEquals(expectedPlaceDetails, placeDetails);

    }

    private Place createPlaceDetails(String placeId) {

        return Place
                .builder()
                .id(null)
                .googlePlaceId(placeId)
                .name("Restaurant Fictício")
                .about(List.of("This is a Brazilian Restaurant"))
                .contact("123456789")
                .businessHours(null)
                .rating(4.5f)
                .isSaved(false)
                .photoReference("photo_reference_1")
                .images(List.of("photo_reference_1"))
                .address("123 Rua Fictícia, Cidade Fictícia")
                .distanceOfLocal(0.0f)
                .build();

    }

}