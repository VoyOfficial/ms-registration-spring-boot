package br.voy.infrastructure.model;

import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import br.voy.domain.entity.Place;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "place", schema = "registration")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(name = "google_place_id", unique = true)
    private String googlePlaceId;

    private String contact;
    private String address;
    private String city;
    private boolean status;
    private Integer ranking;
    @Column(name = "start_recommendation")
    private LocalDate startRecommendation;
    @Column(name = "end_recommendation")
    private LocalDate endRecommendation;
    @Column(name = "created_date")
    private LocalDate createdDate;
    @Column(name = "last_cancel")
    private LocalDate lastCancel;
    private double latitude;
    private double longitude;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private List<PlacePhotoModel> placePhotoModel = new ArrayList<>();

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
        this.createdDate = placeDomain.getCreatedAt();
        this.lastCancel = placeDomain.getLastCancel();
        this.latitude = placeDomain.getLatitude();
        this.longitude = placeDomain.getLongitude();
        this.placePhotoModel = new ArrayList<>();
    }

    @Override
    public Place toDomain() {

        List<PlacePhoto> placePhotos = new ArrayList<>();

        Place domain = Place.builder()
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
                .createdAt(createdDate)
                .lastCancel(lastCancel)
                .latitude(latitude)
                .longitude(longitude)
                .photos(placePhotos)
                .build();

        for (PlacePhotoModel placePhotoModel : placePhotoModel) {
            placePhotos.add(placePhotoModel.toDomain());
        }

        return domain;
    }
}