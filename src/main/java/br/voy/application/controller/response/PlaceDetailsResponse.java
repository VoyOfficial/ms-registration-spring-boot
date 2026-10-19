package br.voy.application.controller.response;

import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PlaceDetailsResponse", description = "Detailed place information")
public class PlaceDetailsResponse {

    @Schema(description = "Google Place id", example = "ChIJPQmNhEMyGZURxuHk44vIaIw")
    private String googlePlaceId;

    @Schema(description = "Establishment name", example = "Hard Rock Cafe")
    private String name;

    @Schema(description = "About text", example = "Restaurant and memorabilia museum")
    private String about;

    @Schema(description = "Contact phone", example = "(54) 3286-4054")
    private String contact;

    @Schema(description = "Weekly business hours")
    private List<BusinessHoursResponse> businessHours;

    @Schema(description = "Average rating", example = "4.6")
    private Float rating;

    @Schema(description = "Total user ratings", example = "320")
    private Integer userRatingsTotal;

    @Schema(description = "Principal photo reference", example = "AeJbb3e...")
    private String principalPhoto;

    @Schema(description = "Photo gallery")
    private List<PlacePhoto> photos;

    @Schema(
            description = "Formatted address",
            example = "Av. das Hortênsias, 2536 - Vila Suica, Gramado - RS")
    private String address;

    @Schema(description = "Place latitude", example = "-29.37812")
    private Double latitude;

    @Schema(description = "Place longitude", example = "-50.86689")
    private Double longitude;

    public static PlaceDetailsResponse toPlaceDetailsResponse(PlaceDetails place) {

        List<BusinessHoursResponse> businessHoursResponseList =
                place.getBusinessHours().stream()
                        .map(BusinessHoursResponse::toBusinessHoursResponse)
                        .collect(Collectors.toList());

        return PlaceDetailsResponse.builder()
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .contact(place.getContact())
                .businessHours(businessHoursResponseList)
                .rating(place.getRating())
                .photos(place.getPhotos())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }
}
