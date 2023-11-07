package src.domain.entity;

import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder
@ToString
@AllArgsConstructor
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
    private List<String> images;
    private String addressType;
    private String addressName;
    private String addressNumber;
    private String address;
    @Schema(example = "Jardim do bairro X")
    private String neighbourhood;

    @Schema(example = "Gramado")
    private String city;

    @Schema(example = "Rio grande do Sul")
    private String state;

    @Schema(example = "RS")
    private String stateCode;

    @Schema(example = "12345-000")
    private String postcode;

    public static Place toNearbyPlace(PlacesSearchResult placeSearchResult) {

        var photoReference = "";

        if (Objects.nonNull(placeSearchResult.photos)) {
            photoReference = Stream.of(placeSearchResult.photos).map(photo -> photo.photoReference).findFirst().orElse("");
        }

        return Place
                .builder()
                .googlePlaceId(placeSearchResult.placeId)
                .name(placeSearchResult.name)
                .about(Stream.of(placeSearchResult.types).collect(Collectors.toList()))
                .rating(placeSearchResult.rating)
                .userRatingsTotal(placeSearchResult.userRatingsTotal)
                .address(placeSearchResult.vicinity)
                .photoReference(photoReference)
                .build();

    }

    public static Place toPlaceDetails(PlaceDetails placeDetails) {

        var imagesReferenceList = Arrays
                .stream(placeDetails.photos)
                .map(photo -> photo.photoReference)
                .collect(Collectors.toList());

        return Place
                .builder()
                .googlePlaceId(placeDetails.placeId)
                .name(placeDetails.name)
                .about(List.of(placeDetails.editorialSummary.overview))
                .contact(placeDetails.formattedPhoneNumber)
                .businessHours(null)
                .rating(placeDetails.rating)
                .userRatingsTotal(placeDetails.userRatingsTotal)
                .images(imagesReferenceList)
                .build();

    }
}
