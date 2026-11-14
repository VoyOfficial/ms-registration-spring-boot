package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.PlacePhotoModel;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlacePhotoJpaRepository extends JpaRepository<PlacePhotoModel, Long> {

    Optional<PlacePhotoModel> findById(Long photoId);

    List<PlacePhotoModel> findByPlaceId(Long placePhotoId);

    @Query(
            "SELECT photo FROM PlacePhotoModel photo JOIN FETCH photo.place place "
                    + "WHERE place.id IN :placeIds "
                    + "AND photo.photoReference = place.principalPhoto")
    List<PlacePhotoModel> findPrincipalPhotos(@Param("placeIds") Collection<Long> placeIds);

    @Query(
            "SELECT photo FROM PlacePhotoModel photo JOIN FETCH photo.place place "
                    + "WHERE place.id IN :placeIds AND photo.id = ("
                    + "SELECT MIN(other.id) FROM PlacePhotoModel other "
                    + "WHERE other.place.id = photo.place.id)")
    List<PlacePhotoModel> findEarliestPhotos(@Param("placeIds") Collection<Long> placeIds);
}
