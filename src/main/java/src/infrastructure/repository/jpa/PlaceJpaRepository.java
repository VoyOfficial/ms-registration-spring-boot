package src.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import src.infrastructure.model.PlaceModel;

import java.util.List;
import java.util.Optional;

public interface PlaceJpaRepository extends JpaRepository<PlaceModel, Long> {

    Optional<PlaceModel> findById(Long placeId);

    Optional<List<PlaceModel>> findByCity(String city);

    @Query("SELECT p FROM place p WHERE p.googlePlaceId = :googlePlaceId")
    PlaceModel findByGooglePlaceId(String googlePlaceId);
}
