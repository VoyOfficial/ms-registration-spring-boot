package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.PlacePhotoModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacePhotoJpaRepository extends JpaRepository<PlacePhotoModel, Long> {

    Optional<PlacePhotoModel> findById(Long photoId);

    List<PlacePhotoModel> findByPlaceId(Long placePhotoId);
}
