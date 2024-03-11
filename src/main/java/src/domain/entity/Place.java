package src.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import lombok.*;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Place {

    private Long id;
    private String googlePlaceId;
    private String name;
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved = false;
    private String principalPhoto; // TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private Float distanceOfLocal; // usar outra api do google

    @Builder(builderMethodName = "placeBuilder")
    public Place(Long id,
                 String googlePlaceId,
                 String name,
                 Float rating,
                 Integer userRatingsTotal,
                 Boolean isSaved,
                 String principalPhoto,
                 Float distanceOfLocal) {
        this.id = id;
        this.googlePlaceId = googlePlaceId;
        this.name = name;
        this.rating = rating;
        this.userRatingsTotal = userRatingsTotal;
        this.isSaved = isSaved;
        this.principalPhoto = principalPhoto;
        this.distanceOfLocal = distanceOfLocal;
    }

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult) {

        var photoReference = "";

        if (Objects.nonNull(placeSearchResult.photos)) {
            photoReference = Stream.of(placeSearchResult.photos).map(photo -> photo.photoReference).findFirst().orElse("");
        }

        return Place
                .placeBuilder()
                .googlePlaceId(placeSearchResult.placeId)
                .name(placeSearchResult.name)
                .rating(placeSearchResult.rating)
                .userRatingsTotal(placeSearchResult.userRatingsTotal)
                .principalPhoto(photoReference)
                .build();

    }

}


