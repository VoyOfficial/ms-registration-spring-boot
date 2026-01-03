package br.voy.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Place {

    private Long id;
    private String googlePlaceId;
    private String name;
    private String about;
    private String contact; // TODO somente no get place details - nao tem na busca de locais proximos - formatted_phone_number na busca de detalhes do local
    private BusinessHours businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved = false;
    private String principalPhoto; // TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private String principalPhotoUrl;
    private List<PlacePhoto> photos;
    private String address;
    private String city;

    @Setter
    private boolean status;

    private Integer ranking;

    @Setter
    private LocalDate startRecommendation;

    @Setter
    private LocalDate endRecommendation;

    @Setter
    private LocalDate createdAt;
    private LocalDate createdDate;
    private LocalDate lastCancel;
    private Float distanceOfLocal; // usar outra api do google
    private double latitude;
    private double longitude;
    @Setter
    private String distanceFromUserLocation;

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

        return Place
                .builder()
                .googlePlaceId(placeSearchResult.placeId)
                .name(placeSearchResult.name)
                .about("") // todo refatorar
                .rating(placeSearchResult.rating)
                .userRatingsTotal(placeSearchResult.userRatingsTotal)
                .address(placeSearchResult.vicinity)
                .principalPhoto(photoReference)
                .build();

    }

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult, String apiKey) {

        var photoReference = extractPhotoReference(placeSearchResult);
        String photoUrl = null;

        if (!photoReference.isEmpty()) {
            photoUrl = String.format("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&photo_reference=%s&key=%s",
                    photoReference, apiKey);
        }

        return Place
                .builder()
                .googlePlaceId(placeSearchResult.placeId)
                .name(placeSearchResult.name)
                .about("") // todo refatorar
                .rating(placeSearchResult.rating)
                .userRatingsTotal(placeSearchResult.userRatingsTotal)
                .address(placeSearchResult.vicinity)
                .principalPhoto(photoReference)
                .principalPhotoUrl(photoUrl)
                .build();

    }

}
