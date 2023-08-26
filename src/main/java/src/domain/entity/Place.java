package src.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@EqualsAndHashCode
public class Place {

    private Long id;
    private String googlePlaceId;
    private String name;
    private List<String> about;
    private String contact; // TODO somente no get place details - nao tem na busca de locais proximos - formatted_phone_number na busca de detalhes do local
    private BusinessHours businessHours; // TODO somente no get place details
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved = false;
    private String photoReference; // TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private String address; // TODO vicinity

    public Place(PlacesSearchResult placeSearchResult) {

        this.googlePlaceId = placeSearchResult.placeId;
        this.name = placeSearchResult.name;
        this.about = Stream.of(placeSearchResult.types).collect(Collectors.toList());
        this.rating = placeSearchResult.rating;
        this.userRatingsTotal = placeSearchResult.userRatingsTotal;
        this.address = placeSearchResult.vicinity;

        var photoReference = "";

        if (Objects.nonNull(placeSearchResult.photos)) {
            photoReference = Stream.of(placeSearchResult.photos).map(photo -> photo.photoReference).findFirst().orElse("");
        }

        this.photoReference = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=" + photoReference;

    }

}
