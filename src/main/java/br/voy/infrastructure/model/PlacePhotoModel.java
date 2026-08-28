package br.voy.infrastructure.model;

import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Base64;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "place_photos", schema = "registration")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacePhotoModel extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JsonIgnore
    @JoinColumn(name = "place_id", nullable = false)
    private PlaceModel place;

    private String photoReference;

    private String photoUrl;

    @Lob
    @Column(name = "image_base64", columnDefinition = "bytea")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] imageBase64;

    private int height;
    private int width;
    private String htmlAttributions;

    public PlacePhotoModel(PlacePhoto placePhoto) {
        this.id = placePhoto.getId();
        this.place = PlaceModel.builder().id(placePhoto.getPlaceId()).build();
        this.photoReference = placePhoto.getPhotoReference();
        this.photoUrl = placePhoto.getPhotoUrl();
        this.height = placePhoto.getHeight();
        this.width = placePhoto.getWidth();
        this.htmlAttributions = placePhoto.getHtmlAttributions();

        // Aqui você precisa converter a String Base64 para byte[]
        if (placePhoto.getImageBase64() != null) {
            this.imageBase64 = Base64.getDecoder().decode(placePhoto.getImageBase64());
        }
    }

    public void setPlace(PlaceModel place) {
        this.place = place;
    }

    @Override
    public PlacePhoto toDomain() {
        return PlacePhoto.builder()
                .id(id)
                .placeId(place != null ? place.getId() : null)
                .photoReference(photoReference)
                .photoUrl(photoUrl)
                .imageBase64(
                        imageBase64 != null
                                ? new String(imageBase64)
                                : null) // Converte byte[] para String Base64
                .height(height)
                .width(width)
                .htmlAttributions(htmlAttributions)
                .build();
    }
}
