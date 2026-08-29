package br.voy.application.controller.response;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
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
    private String photo;
    private List<PlacePhoto> photos;
    private Float distanceOfLocal = null;
    private double latitude;
    private double longitude;
    private String distanceFromUserLocation;

    // Construtor padrão (necessário para frameworks como Jackson e JPA)
    public PlaceResponse() {}

    // Construtor completo
    public PlaceResponse(Place place) {
        this(place, false); // Default: user not logged or place not saved
    }

    // Construtor com isSaved
    public PlaceResponse(Place place, Boolean isSaved) {
        this.id = place.getId();
        this.googlePlaceId = place.getGooglePlaceId();
        this.name = place.getName();
        this.contact = place.getContact();
        this.address = place.getAddress();
        this.city = place.getCity();
        this.about = place.getAbout();
        this.rating = place.getRating();
        this.ranking = place.getRanking();
        this.userRatingsTotal = place.getUserRatingsTotal();
        this.isSaved = isSaved;
        this.photoReference = place.getPrincipalPhoto();
        this.photo = stripApiKey(place.getPrincipalPhotoUrl());
        this.photos = sanitizePhotos(place.getPhotos());
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.distanceFromUserLocation = place.getDistanceFromUserLocation();
    }

    // Método de conversão de Domain para Response (exclui o campo 'about' para recommended places)
    public static PlaceResponse fromDomain(Place place) {
        return fromDomain(place, false); // Default: user not logged or place not saved
    }

    // Método de conversão com isSaved calculado
    public static PlaceResponse fromDomain(Place place, Boolean isSaved) {
        return PlaceResponse.builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .contact(place.getContact())
                .address(place.getAddress())
                .city(place.getCity())
                .about(place.getAbout())
                .rating(place.getRating())
                .ranking(place.getRanking())
                .userRatingsTotal(place.getUserRatingsTotal())
                .isSaved(isSaved)
                .photoReference(place.getPrincipalPhoto())
                .photo(stripApiKey(place.getPrincipalPhotoUrl()))
                .photos(sanitizePhotos(place.getPhotos()))
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .distanceFromUserLocation(place.getDistanceFromUserLocation())
                .build();
    }

    public static PlaceResponse toNearbyPlaceResponse(Place place) {
        return toNearbyPlaceResponse(place, false); // Default: user not logged or place not saved
    }

    public static PlaceResponse toNearbyPlaceResponse(Place place, Boolean isSaved) {
        return PlaceResponse.builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .isSaved(isSaved)
                .photoReference(place.getPrincipalPhoto())
                .photo(getPrincipalPhotoBase64(place))
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .build();
    }

    private static String getPrincipalPhotoBase64(Place place) {
        if (place.getPhotos() == null || place.getPhotos().isEmpty()) {
            return null;
        }

        return place.getPhotos().stream()
                .filter(
                        p ->
                                p.getPhotoReference() != null
                                        && p.getPhotoReference().equals(place.getPrincipalPhoto()))
                .map(PlacePhoto::getImageBase64)
                .findFirst()
                .orElse(null);
    }

    private static List<PlacePhoto> sanitizePhotos(List<PlacePhoto> photos) {
        if (photos == null) {
            return null;
        }
        return photos.stream()
                .map(
                        photo ->
                                PlacePhoto.builder()
                                        .id(photo.getId())
                                        .placeId(photo.getPlaceId())
                                        .photoReference(photo.getPhotoReference())
                                        .photoUrl(stripApiKey(photo.getPhotoUrl()))
                                        .imageBase64(photo.getImageBase64())
                                        .height(photo.getHeight())
                                        .width(photo.getWidth())
                                        .htmlAttributions(photo.getHtmlAttributions())
                                        .build())
                .toList();
    }

    static String stripApiKey(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        if (url.toLowerCase().contains("key=")) {
            return null;
        }
        return url;
    }
}
