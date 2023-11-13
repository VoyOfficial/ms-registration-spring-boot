package src.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import src.domain.entity.Place;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "place")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String googlePlaceId;
    private String name;
    private String contact;
    private String address;
    private String city;
    private boolean status;
    private Integer ranking;
    private Date startRecommendation;
    private Date endRecommendation;
    private Date createdDate;
    private Date lastCancel;
    private double latitude;
    private double longitude;

    public PlaceModel(Place placeDomain) {
        this.id = placeDomain.getId();
        this.googlePlaceId = placeDomain.getGooglePlaceId();
        ;
        this.name = placeDomain.getName();
        this.contact = placeDomain.getContact();
        this.address = placeDomain.getAddress();
        this.city = placeDomain.getCity();
        this.status = placeDomain.isStatus();
        this.ranking = placeDomain.getRanking();
        this.startRecommendation = placeDomain.getStartRecommendation();
        this.endRecommendation = placeDomain.getEndRecommendation();
        this.createdDate = placeDomain.getCreatedDate();
        this.lastCancel = placeDomain.getLastCancel();
        this.latitude = placeDomain.getLatitude();
        this.longitude = placeDomain.getLongitude();
    }

    @Override
    public Place toDomain() {
        return Place.builder()
                .id(id)
                .googlePlaceId(googlePlaceId)
                .name(name)
                .contact(contact)
                .address(address)
                .city(city)
                .status(status)
                .ranking(ranking)
                .startRecommendation(startRecommendation)
                .endRecommendation(endRecommendation)
                .createdDate(createdDate)
                .lastCancel(lastCancel)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}


