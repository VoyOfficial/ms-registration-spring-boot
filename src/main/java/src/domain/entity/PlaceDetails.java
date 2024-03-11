package src.domain.entity;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlaceDetails extends Place {

    private String about;
    private String contact;
    private List<BusinessHours> businessHours;
    private List<String> images;// TODO Acessar https://developers.google.com/maps/documentation/places/web-service/photos?hl=pt-br
    private String address;

    @Builder
    public PlaceDetails(Long id,
                        String googlePlaceId,
                        String name,
                        Float rating,
                        Integer userRatingsTotal,
                        Boolean isSaved,
                        String principalPhoto,
                        Float distanceOfLocal,
                        String about,
                        String contact,
                        List<BusinessHours> businessHours,
                        List<String> images,
                        String address) {
        super(id, googlePlaceId, name, rating, userRatingsTotal, isSaved, principalPhoto, distanceOfLocal);
        this.about = about;
        this.contact = contact;
        this.businessHours = businessHours;
        this.images = images;
        this.address = address;
    }

}
