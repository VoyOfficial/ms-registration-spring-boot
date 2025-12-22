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

    private String about;
    private String contact;
    private String address;
    private String city;
    private Float rating;

    @Column(name = "userratingstotal")
    private Integer userRatingsTotal;

    @Column(name = "issaved")
    private Boolean isSaved;

    @Column(name = "principal_photo")
    private String principalPhoto;

    @Column(name = "principal_photo_url")
    private String principalPhotoUrl;

    private Boolean status;
    private Integer ranking;

    @Column(name = "start_recommendation")
    private LocalDate startRecommendation;

    @Column(name = "end_recommendation")
    private LocalDate endRecommendation;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "last_cancel")
    private LocalDate lastCancel;

    private Double latitude;
    private Double longitude;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "place", fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonIgnore
    private List<PlacePhotoModel> placePhotoModel = new ArrayList<>();

    public PlaceModel(Place placeDomain) {
        // Não copiar o ID - deixar o Hibernate gerar automaticamente para novos registros
        this.googlePlaceId = placeDomain.getGooglePlaceId();
        this.name = placeDomain.getName();
        this.about = placeDomain.getAbout();
        this.contact = placeDomain.getContact();
        this.address = placeDomain.getAddress();
        this.city = placeDomain.getCity();
        this.rating = placeDomain.getRating();
        this.userRatingsTotal = placeDomain.getUserRatingsTotal();
        this.isSaved = placeDomain.getIsSaved();
        this.principalPhoto = placeDomain.getPrincipalPhoto();
        this.principalPhotoUrl = placeDomain.getPrincipalPhotoUrl();
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

        // Carregar fotos se existirem
        if (placePhotoModel != null && !placePhotoModel.isEmpty()) {
            for (PlacePhotoModel photoModel : placePhotoModel) {
                placePhotos.add(photoModel.toDomain());
            }
        }

        Place domain = Place.builder()
                .id(id)
                .googlePlaceId(googlePlaceId)
                .name(name)
                .about(about)
                .contact(contact)
                .address(address)
                .city(city)
                .rating(rating)
                .userRatingsTotal(userRatingsTotal)
                .isSaved(isSaved != null ? isSaved : false)
                .principalPhoto(principalPhoto)
                .principalPhotoUrl(principalPhotoUrl)
                .status(status != null ? status : false)
                .ranking(ranking)
                .startRecommendation(startRecommendation)
                .endRecommendation(endRecommendation)
                .createdAt(createdDate)
                .lastCancel(lastCancel)
                .latitude(latitude != null ? latitude : 0.0)
                .longitude(longitude != null ? longitude : 0.0)
                .photos(placePhotos)
                .build();


        return domain;
    }
}