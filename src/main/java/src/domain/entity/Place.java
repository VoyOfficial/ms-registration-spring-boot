package src.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@EqualsAndHashCode
public class Place {

    private Long id;
    private String googlePlaceId;
    private String name;
    private List<String> about;
    private String contact; // TODO somente no get place details
    private BusinessHours businessHours; // TODO somente no get place details
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved = false;
    private Boolean isFavourite = false;
    private List<String> comments; // TODO somente no get place details
    private String photoReference; // TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br

    public Place(PlacesSearchResult placeSearchResult) {

        this.googlePlaceId = placeSearchResult.placeId;
        this.name = placeSearchResult.name;
        this.about = Stream.of(placeSearchResult.types).collect(Collectors.toList());
        this.rating = placeSearchResult.rating;
        this.userRatingsTotal = placeSearchResult.userRatingsTotal;

        var photoReference = Stream.of(placeSearchResult.photos).map(photo -> photo.photoReference).findFirst().orElse("");

        this.photoReference = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=" + photoReference;

    }

}
