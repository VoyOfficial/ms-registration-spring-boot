package src.domain.entity;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlaceDetails extends Place {

    private String about;
    private String contact;
    private List<BusinessHours> businessHours;
    private List<String> images;// TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private String address;

    @Builder
    public PlaceDetails(Long id,
                        String googlePlaceId,
                        String name,
                        Float rating,
                        Integer userRatingsTotal,
                        Boolean isSaved,
                        String principalPhoto,
                        Float distanceOfLocal,
                        String about,
                        String contact,
                        List<BusinessHours> businessHours,
                        List<String> images,
                        String address) {
        super(id, googlePlaceId, name, rating, userRatingsTotal, isSaved, principalPhoto, distanceOfLocal);
        this.about = about;
        this.contact = contact;
        this.businessHours = businessHours;
        this.images = images;
        this.address = address;
    }

    public static PlaceDetails toPlaceDetailsByGoogle(com.google.maps.model.PlaceDetails placeDetails) {

        var imagesReferenceList = Arrays
                .stream(placeDetails.photos)
                .map(photo -> photo.photoReference)
                .collect(Collectors.toList());

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
                .images(imagesReferenceList)
                .address(placeDetails.formattedAddress)
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
