package src.domain.mapper;

import com.google.maps.model.Photo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.domain.entity.BusinessHours;
import src.domain.entity.PlaceDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PlaceDetailsMapper {

    private final String URL_PHOTOS = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=";
    private final int MAX_WIDTH = 1920;
    private final int MAX_HEIGHT = 1080;

    @Value("${places.api.key}")
    public String API_KEY;

    public PlaceDetails toPlaceDetailsByGoogle(com.google.maps.model.PlaceDetails googlePlaceDetails) {

        var imagesLinks = createImageLinks(googlePlaceDetails.photos);

        String about = getAbout(googlePlaceDetails);
        String contact = getContact(googlePlaceDetails);

        List<BusinessHours> businessHoursList = BusinessHours.toBusinessHoursList(googlePlaceDetails.openingHours);

        return PlaceDetails
                .builder()
                .googlePlaceId(googlePlaceDetails.placeId)
                .name(googlePlaceDetails.name)
                .about(about)
                .contact(contact)
                .businessHours(businessHoursList)
                .rating(googlePlaceDetails.rating)
                .userRatingsTotal(googlePlaceDetails.userRatingsTotal)
                .images(imagesLinks)
                .address(googlePlaceDetails.formattedAddress)
                .build();
    }

    private List<String> createImageLinks(Photo[] photos) {

        return Arrays
                .stream(photos)
                .map(photo -> URL_PHOTOS +
                        photo.photoReference +
                        "&maxwidth=" + MAX_WIDTH +
                        "&maxheight=" + MAX_HEIGHT +
                        "&key=" + API_KEY
                ).collect(Collectors.toList());

    }

    private String getAbout(com.google.maps.model.PlaceDetails placeDetails) {

        String about = "";
        if (Objects.nonNull(placeDetails.editorialSummary)) {
            about = placeDetails.editorialSummary.overview;
        }
        return about;

    }

    private String getContact(com.google.maps.model.PlaceDetails placeDetails) {

        String contact = "";
        if (Objects.nonNull(placeDetails.formattedPhoneNumber)) {
            contact = placeDetails.formattedPhoneNumber;
        }
        return contact;

    }

}
