package br.voy.domain.entity;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Data
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class PlaceDetails {

    private String googlePlaceId;
    private String name;
    private String about;
    private String contact;
    private List<BusinessHours> businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    private String address;
    private List<PlacePhoto> photos; // TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private Double latitude;
    private Double longitude;

    public static PlaceDetails toPlaceDetailsByGoogle(com.google.maps.model.PlaceDetails placeDetails) {

        String about = getAbout(placeDetails);
        String contact = getContact(placeDetails);

        List<BusinessHours> businessHoursList = BusinessHours.toBusinessHoursList(placeDetails.openingHours);

        return PlaceDetails
                .builder()
                .googlePlaceId(placeDetails.placeId)
                .name(placeDetails.name)
                .about(about)
                .contact(contact)
                .businessHours(businessHoursList)
                .rating(placeDetails.rating)
                .userRatingsTotal(placeDetails.userRatingsTotal)
                .address(placeDetails.formattedAddress)
                .latitude(placeDetails.geometry.location.lat)
                .longitude(placeDetails.geometry.location.lng)
                .build();

    }

    public static PlaceDetails toPlaceDetailsByGoogleAndPhotos(com.google.maps.model.PlaceDetails placeDetails, List<PlacePhoto> placePhotos) {

        String about = getAbout(placeDetails);
        String contact = getContact(placeDetails);

        List<BusinessHours> businessHoursList = BusinessHours.toBusinessHoursList(placeDetails.openingHours);

        return PlaceDetails
                .builder()
                .googlePlaceId(placeDetails.placeId)
                .name(placeDetails.name)
                .about(about)
                .contact(contact)
                .businessHours(businessHoursList)
                .rating(placeDetails.rating)
                .photos(placePhotos)
                .userRatingsTotal(placeDetails.userRatingsTotal)
                .address(placeDetails.formattedAddress)
                .latitude(placeDetails.geometry.location.lat)
                .longitude(placeDetails.geometry.location.lng)
                .build();

    }


    private static String getAbout(com.google.maps.model.PlaceDetails placeDetails) {

        String about = "";
        if (Objects.nonNull(placeDetails.editorialSummary)) {
            about = placeDetails.editorialSummary.overview;
        }
        return about;

    }

    private static String getContact(com.google.maps.model.PlaceDetails placeDetails) {

        String contact = "";
        if (Objects.nonNull(placeDetails.formattedPhoneNumber)) {
            contact = placeDetails.formattedPhoneNumber;
        }
        return contact;

    }

}
