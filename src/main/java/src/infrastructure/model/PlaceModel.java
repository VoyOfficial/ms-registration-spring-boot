package src.infrastructure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import src.domain.entity.Place;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "place")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true)
    private String googlePlaceId;

    private String contact;
    private String address;
    private String city;
    private boolean status;
    private Integer ranking;
    private LocalDate startRecommendation;
    private LocalDate endRecommendation;
    private LocalDate createdAt;
    private LocalDate lastCancel;
    private double latitude;
    private double longitude;

    public PlaceModel(Place placeDomain) {
        this.id = placeDomain.getId();
        this.googlePlaceId = placeDomain.getGooglePlaceId();
        this.name = placeDomain.getName();
        this.contact = placeDomain.getContact();
        this.address = placeDomain.getAddress();
        this.city = placeDomain.getCity();
        this.status = placeDomain.isStatus();
        this.ranking = placeDomain.getRanking();
        this.startRecommendation = placeDomain.getStartRecommendation();
        this.endRecommendation = placeDomain.getEndRecommendation();
        this.createdAt = placeDomain.getCreatedAt();
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
                .createdAt(createdAt)
                .lastCancel(lastCancel)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}


