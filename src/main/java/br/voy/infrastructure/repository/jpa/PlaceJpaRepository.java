package br.voy.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import br.voy.infrastructure.model.PlaceModel;

import java.util.List;
import java.util.Optional;

public interface PlaceJpaRepository extends JpaRepository<PlaceModel, Long> {

    Optional<PlaceModel> findById(Long placeId);

    Optional<List<PlaceModel>> findByCity(String city);

    Optional<PlaceModel> findPlaceByGooglePlaceId(String googlePlaceId);

}
