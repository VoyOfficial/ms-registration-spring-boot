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
            "SELECT photo.id, place.id, photo.photoReference, place.principalPhoto "
                    + "FROM PlacePhotoModel photo JOIN photo.place place "
                    + "WHERE place.id IN :placeIds "
                    + "AND (photo.imageBase64 IS NULL OR length(photo.imageBase64) <= :maxBytes)")
    List<Object[]> findPhotoRefsByPlaceIds(
            @Param("placeIds") Collection<Long> placeIds, @Param("maxBytes") int maxBytes);
}
