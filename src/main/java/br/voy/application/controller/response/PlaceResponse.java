package br.voy.application.controller.response;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "PlaceResponse",
        description = "Place summary returned by nearby and recommendation endpoints")
public class PlaceResponse {

    @Schema(description = "Internal place id", example = "12")
    private Long id;

    @Schema(description = "Google Place id", example = "ChIJsdUuyRc1GZURg7Hy1kfaAeU")
    private String googlePlaceId;

    @Schema(description = "Establishment name", example = "Space Adventure Canela")
    private String name;

    @Schema(description = "Contact phone", example = "(54) 3286-1055")
    private String contact;

    @Schema(
            description = "Formatted address",
            example = "Av. Ernani Kroeff Fleck, 960 - Vila Suica, Canela - RS, 95684-180")
    private String address;

    @Schema(description = "City name", example = "Canela")
    private String city;

    @Schema(description = "Short description about the place", example = "Theme park for families")
    private String about;

    @Schema(description = "Business hours for a single day when present")
    private BusinessHoursResponse businessHours;

    @Schema(description = "Average rating", example = "4.5")
    private Float rating;

    @Schema(description = "Recommendation ranking (lower is better)", example = "1")
    private Integer ranking;

    @Schema(description = "Total user ratings", example = "128")
    private Integer userRatingsTotal;

    @Schema(description = "Whether the authenticated user saved this place", example = "false")
    private Boolean isSaved;

    @Schema(description = "Principal photo reference", example = "AeJbb3e...")
    private String photoReference;

    @Schema(description = "Principal photo URL or base64 payload without API key")
    private String photo;

    @Schema(description = "Photo gallery for the place")
    private List<PlacePhoto> photos;

    @Schema(description = "Legacy distance value in meters", example = "1224.0")
    private Float distanceOfLocal = null;

    @Schema(description = "Place latitude", example = "-29.35963")
    private double latitude;

    @Schema(description = "Place longitude", example = "-50.83543")
    private double longitude;

    @Schema(description = "Human-readable distance from the user", example = "1.224 km")
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
