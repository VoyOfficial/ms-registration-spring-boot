package br.voy.application.controller.response;

import br.voy.domain.entity.Place;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
    private String contact;
    private String address;
    private String city;
    private String about;
    private BusinessHoursResponse businessHours;
    private Float rating;
    private Integer ranking;
    private Integer userRatingsTotal;
    private Boolean isSaved;
    private String photoReference;
    private List<String> images;
    private Float distanceOfLocal = null;
    private double latitude;
    private double longitude;
    private String distanceFromUserLocation;

    // Construtor padrão (necessário para frameworks como Jackson e JPA)
    public PlaceResponse() {
    }

    // Construtor completo
    public PlaceResponse(Place place) {
        this.id = place.getId();
        this.googlePlaceId = place.getGooglePlaceId();
        this.name = place.getName();
        this.contact = place.getContact();
        this.address = place.getAddress();
        this.city = place.getCity();
        this.about = place.getAbout();
//        this.businessHours = BusinessHoursResponse.toBusinessHoursResponse(place.getBusinessHours());
        this.rating = place.getRating();
        this.ranking = place.getRanking();
        this.userRatingsTotal = place.getUserRatingsTotal();
        this.isSaved = place.getIsSaved();
        this.photoReference = place.getPrincipalPhoto();
        this.images = place.getImages();
//        this.distanceOfLocal = place.getDistanceOfLocal();
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.distanceFromUserLocation = place.getDistanceFromUserLocation();
    }

    // Método de conversão de Domain para Response
    public static PlaceResponse fromDomain(Place place) {
        return new PlaceResponse(place);
    }

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
                .photoReference(place.getPrincipalPhoto())
                .build();

    }
}

