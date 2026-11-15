package br.voy.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Place {

    /** Stored image ceiling for list payloads (nearby, search, recommendations). */
    public static final int MAX_LIST_PHOTO_BYTES = 150_000;

    /**
     * Largest base64 string {@link #MAX_LIST_PHOTO_BYTES} of raw image can produce. List responses
     * drop a photo above this so Google-sourced and DB-sourced paths share one rule.
     */
    public static final int MAX_LIST_PHOTO_BASE64_CHARS = ((MAX_LIST_PHOTO_BYTES + 2) / 3) * 4;

    private Long id;
    private String googlePlaceId;
    private String name;
    private String about;
    private String
            contact; // TODO somente no get place details - nao tem na busca de locais proximos -
    // formatted_phone_number na busca de detalhes do local
    private BusinessHours businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    @Setter private Boolean isSaved = false;
    private String principalPhoto; // TODO Acessar
    // https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private String principalPhotoUrl;
    private List<PlacePhoto> photos;
    private String address;
    private String city;
    private String state;

    @Setter private boolean status;

    private Integer ranking;

    @Setter private LocalDate startRecommendation;

    @Setter private LocalDate endRecommendation;

    @Setter private LocalDate createdAt;
    private LocalDate createdDate;
    private LocalDate lastCancel;
    private Float distanceOfLocal; // usar outra api do google
    private double latitude;
    private double longitude;
    @Setter private String distanceFromUserLocation;
    private String googleTypes;

    public List<PlacePhoto> principalPhotos() {
        if (photos == null || photos.isEmpty()) {
            return List.of();
        }

        PlacePhoto fallback = null;
        for (PlacePhoto photo : photos) {
            String encoded = photo.getImageBase64();
            if (encoded == null || encoded.isBlank()) {
                continue;
            }
            if (encoded.length() > MAX_LIST_PHOTO_BASE64_CHARS) {
                continue;
            }
            if (fallback == null) {
                fallback = photo;
            }
            if (principalPhoto != null
                    && !principalPhoto.isBlank()
                    && principalPhoto.equals(photo.getPhotoReference())) {
                return List.of(photo);
            }
        }
        return fallback == null ? List.of() : List.of(fallback);
    }

    public String principalPhotoBase64() {
        List<PlacePhoto> selected = principalPhotos();
        if (selected.isEmpty()) {
            return null;
        }
        return selected.get(0).getImageBase64();
    }

    private static String extractPhotoReference(PlacesSearchResult placeSearchResult) {
        if (Objects.nonNull(placeSearchResult.photos)) {
            return Stream.of(placeSearchResult.photos)
                    .map(photo -> photo.photoReference)
                    .findFirst()
                    .orElse("");
        }
        return "";
    }

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult) {

        var photoReference = extractPhotoReference(placeSearchResult);

        double latitude = 0.0;
        double longitude = 0.0;

        if (Objects.nonNull(placeSearchResult.geometry)
                && Objects.nonNull(placeSearchResult.geometry.location)) {
            latitude = placeSearchResult.geometry.location.lat;
            longitude = placeSearchResult.geometry.location.lng;
        }

        String address =
                placeSearchResult.vicinity != null && !placeSearchResult.vicinity.isBlank()
                        ? placeSearchResult.vicinity
                        : placeSearchResult.formattedAddress;

        return Place.builder()
                .googlePlaceId(placeSearchResult.placeId)
                .name(placeSearchResult.name)
                .about("") // todo refatorar
                .rating(placeSearchResult.rating)
                .userRatingsTotal(placeSearchResult.userRatingsTotal)
                .address(address)
                .principalPhoto(photoReference)
                .googleTypes(joinGoogleTypes(placeSearchResult))
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult, String apiKey) {
        return toNearbyPlace(placeSearchResult);
    }

    private static String joinGoogleTypes(PlacesSearchResult placeSearchResult) {
        if (placeSearchResult.types == null || placeSearchResult.types.length == 0) {
            return null;
        }
        return String.join(",", placeSearchResult.types);
    }
}
