package src.domain.entity;

import com.google.maps.model.PlacesSearchResult;
import lombok.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Data
@Builder
@ToString
@AllArgsConstructor
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
    private List<String> images;
    private String address;
    private String city;
    private boolean status;
    private Integer ranking;
    private Date startRecommendation;
    private Date endRecommendation;
    private Date createdDate;
    private Date lastCancel;
    private Float distanceOfLocal; // usar outra api do google
    private double latitude;
    private double longitude;

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult) {

        var photoReference = "";

        if (Objects.nonNull(placeSearchResult.photos)) {
            photoReference = Stream.of(placeSearchResult.photos).map(photo -> photo.photoReference).findFirst().orElse("");
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
                .build();

    }

}
