package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.PlacePhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlacePhotoJpaRepository extends JpaRepository<PlacePhotoModel, Long> {

    Optional<PlacePhotoModel> findById(Long photoId);

    List<PlacePhotoModel> findByPlaceId(Long placePhotoId);

}
