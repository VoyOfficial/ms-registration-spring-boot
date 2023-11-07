package src.infrastructure.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import src.domain.entity.Place;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity(name = "place")
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class PlaceModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Schema(example = "123")
//    @NotBlank
//    private String googleId; //old document

    @Schema(example = "padaria do zé")
    private String name;

    @Schema(example = "pãe & doces")
    private String about; //porque é uma lista? futuro vamos ter as caracteristicas como hashmap ex {Formas de pagamento, [debito, credito]}

    @Schema(example = "+5511987654321")
    private String contact;

//    @Schema(example = "avenida")
//    private String addressType;
//
//    @Schema(example = "professor João")
//    private String addressName;
//
//    @Schema(example = "31")
//    private String addressNumber;
//
//    @Schema(example = "Jardim do bairro X")
//    private String neighbourhood;

    @Schema(example = "Gramado")
    private String city;

    @Schema(example = "Rio grande do Sul")
    private String state;

//    @Schema(example = "RS")
//    private String stateCode;

//    @Schema(example = "12345-000")
//    private String postcode;

//    @Schema(example = "Rua. professor João, 31 - Jardim do bairro X, Gramado - RS, 12345-000")
//    private String fullAddress;

    @Schema(example = "4.9")
    private Float rating;

    @Schema(example = "true")
    private boolean isSaved;

    @Schema(example = "49")
    private Integer userratingstotal;

//    @Schema(example = "[{weekDay: segunda, intervalId: 123}")
//    private List<BusinessHours> businessHours;

    //place(1, n) <- -> (0, 1)img
//    @Schema(example = "123abc, 456def")
//    private List<String> placeImages;

    public PlaceModel(Place placeDomain) {

        this.id = placeDomain.getId();
//        this.googleId = placeDomain.getGooglePlaceId();
        this.name = placeDomain.getName();
        this.about = placeDomain.getAbout().get(0);
        this.contact = placeDomain.getContact();
//        this.addressType = placeDomain.getAddressType();
//        this.addressName = placeDomain.getAddressName();
//        this.addressNumber = placeDomain.getAddressNumber();
//        this.neighbourhood = placeDomain.getNeighbourhood();
        this.city = placeDomain.getCity();
        this.state = placeDomain.getState();
//        this.stateCode = placeDomain.getStateCode();
//        this.postcode = placeDomain.getPostcode();
//        this.placeImages = placeDomain.getImages();
        this.rating = placeDomain.getRating();
        this.userratingstotal = placeDomain.getUserRatingsTotal();
//        this.businessHours = List.of(placeDomain.getBusinessHours());
        this.isSaved = placeDomain.getIsSaved();

    }

    @Override
    public Place toDomain() {
        return Place
                .builder()
                .id(id)
//                .googlePlaceId(googleId)
                .name(name)
                .about(List.of(about))
                .contact(contact)
//                .addressType(addressType)
//                .addressName(addressName)
//                .addressNumber(addressNumber)
//                .address(fullAddress)
//                .neighbourhood(neighbourhood)
                .city(city)
                .state(state)
//                .stateCode(stateCode)
//                .postcode(postcode)
//                .images(placeImages)
                .isSaved(isSaved)
                .rating(rating)
                .userRatingsTotal(userratingstotal)
//                .businessHours(businessHours.get(0))
                .build();

    }
}
