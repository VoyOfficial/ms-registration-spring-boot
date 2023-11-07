package src.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import src.domain.entity.Place;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceResponse {

    private Long id;
    private String googlePlaceId;
    private String name;
    private List<String> about;
    private String contact;
    private BusinessHoursResponse businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved;
    private String photoReference;
    private List<String> images;
    private String address;
    private float distanceOfLocal;

    public static PlaceResponse toNearbyPlaceResponse(Place place) {

        return PlaceResponse
                .builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .isSaved(place.getIsSaved())
                .photoReference(place.getPhotoReference())
                .build();

    }

    public static PlaceResponse toPlaceDetailsResponse(Place place) {

        return PlaceResponse
                .builder()
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .contact(place.getContact())
                .businessHours(null)
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .images(place.getImages())
                .address(place.getAddress())
                .build();

    }

}

