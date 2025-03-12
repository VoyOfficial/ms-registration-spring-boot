package br.voy.application.controller.response;

import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import br.voy.domain.entity.PlaceDetails;

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
    private List<PlacePhoto> photos;
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
                .photos(place.getPhotos())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .build();

    }

}

