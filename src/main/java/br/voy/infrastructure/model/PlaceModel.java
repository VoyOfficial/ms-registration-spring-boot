package br.voy.infrastructure.model;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private String state;
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

    @Column(name = "distanceoflocal")
    private Float distanceOfLocal;

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "place", fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonIgnore
    private List<PlacePhotoModel> placePhotoModel = new ArrayList<>();

    public PlaceModel(Place placeDomain) {
        // Copiar o ID se existir para que o Hibernate saiba que é uma atualização
        if (placeDomain.getId() != null) {
            this.id = placeDomain.getId();
        }
        this.googlePlaceId = placeDomain.getGooglePlaceId();
        this.name = placeDomain.getName();
        this.about = placeDomain.getAbout();
        this.contact = placeDomain.getContact();
        this.address = placeDomain.getAddress();
        this.city = placeDomain.getCity();
        this.state = placeDomain.getState();
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
        this.distanceOfLocal = placeDomain.getDistanceOfLocal();
        // Populate placePhotoModel from domain photos (if any)
        populatePhotosFromDomain(placeDomain);
    }

    @Override
    public Place toDomain() {

        List<PlacePhoto> placePhotos = convertPhotosToDomain();

        Place domain =
                Place.builder()
                        .id(id)
                        .googlePlaceId(googlePlaceId)
                        .name(name)
                        .about(about)
                        .contact(contact)
                        .address(address)
                        .city(city)
                        .state(state)
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
                        .distanceOfLocal(distanceOfLocal != null ? distanceOfLocal : 0.0f)
                        .photos(placePhotos)
                        .build();

        return domain;
    }

    private List<PlacePhoto> convertPhotosToDomain() {
        List<PlacePhoto> placePhotos = new ArrayList<>();

        if (placePhotoModel != null && !placePhotoModel.isEmpty()) {
            for (PlacePhotoModel photoModel : placePhotoModel) {
                placePhotos.add(photoModel.toDomain());
            }
        }

        return placePhotos;
    }

    // Helper to populate the JPA model photos from the domain photos when constructing PlaceModel
    private void populatePhotosFromDomain(Place placeDomain) {
        this.placePhotoModel = new ArrayList<>();
        if (placeDomain != null
                && placeDomain.getPhotos() != null
                && !placeDomain.getPhotos().isEmpty()) {
            for (PlacePhoto photo : placeDomain.getPhotos()) {
                var photoModel = new PlacePhotoModel(photo);
                // ensure bi-directional association points to this PlaceModel
                photoModel.setPlace(this);
                this.placePhotoModel.add(photoModel);
            }
        }
    }
}
