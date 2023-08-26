package src.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import src.domain.entity.Place;

import java.util.List;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaceResponse {

    private Long id;
    private String googlePlaceId;
    private String name;
    private List<String> about;
    private String contact;
    private BusinessHoursResponse businessHours;
    private Float rating;
    private Integer userRatingsTotal;
    private Boolean isSaved;
    private String photoReference;
    private String address;

    public PlaceResponse(Place place) {

        this.id = place.getId();
        this.googlePlaceId = place.getGooglePlaceId();
        this.name = place.getName();
        this.about = place.getAbout();
        this.rating = place.getRating();
        this.userRatingsTotal = place.getUserRatingsTotal();
        this.address = place.getAddress();
        this.isSaved = place.getIsSaved();
        this.photoReference = place.getPhotoReference();

    }

}

