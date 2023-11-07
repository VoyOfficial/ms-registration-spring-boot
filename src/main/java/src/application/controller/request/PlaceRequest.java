package src.application.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import src.domain.entity.BusinessHours;
import src.domain.entity.Place;

import java.util.List;

@Getter
@Builder
public class PlaceRequest extends AbstractRequest {

    //isSaved será preenchido pelo usuário
    //rating e usertotalrating será preenchido pelo google

    @Schema(example = "123")
    private String googleId;

    @Schema(example = "padaria do zé")
    private String name;

    @Schema(example = "pãe & doces")
    private List<String> about; //porque é uma lista? futuro vamos ter as caracteristicas como hashmap ex {Formas de pagamento, [debito, credito]}

    @Schema(example = "+5511987654321")
    private String contact;

    @Schema(example = "avenida")
    private String addressType;

    @Schema(example = "professor João")
    private String addressName;

    @Schema(example = "31")
    private String addressNumber;

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

    @Schema(example = "Rua. professor João, 31 - Jardim do bairro X, Gramado - RS, 12345-000")
    private String fullAddress;

    @Schema(example = "[{weekDay: segunda, intervalId: 123}")
    private List<BusinessHours> businessHours;

    @Schema(example = "123abc, 456def")
    private List<String> placeImages;

    @Override
    public Place toDomain() {
        return Place.builder()
//                .googlePlaceId(googleId)
                .name(name)
                .about(about)
                .contact(contact)
                .address(addressType + ". " + addressName + ", " + addressNumber + " - " + neighbourhood + ", " + city + " - " + stateCode + ", " + postcode)
//                .businessHours(businessHours.get(0))
//                .images(placeImages)
                .build();
    }
}
