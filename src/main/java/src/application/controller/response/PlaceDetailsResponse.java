package src.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import src.domain.entity.Place;
import src.domain.entity.PlaceDetails;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceDetailsResponse {

    private String googlePlaceId;
    private String name;
    private String about;
    private String contact;
    private List<BusinessHoursResponse> businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    private String principalPhoto;
    private List<String> images;
    private String address;

    public static PlaceDetailsResponse toPlaceDetailsResponse(PlaceDetails place) {

        List<BusinessHoursResponse> businessHoursResponseList = place.getBusinessHours()
                .stream()
                .map(BusinessHoursResponse::toBusinessHoursResponse)
                .collect(Collectors.toList());

        return PlaceDetailsResponse
                .builder()
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .contact(place.getContact())
                .businessHours(businessHoursResponseList)
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .images(place.getImages())
                .address(place.getAddress())
                .build();

    }

}

