package src.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import src.domain.entity.Place;
import src.infrastructure.model.PlaceModel;

import java.util.List;
import java.util.Optional;

public interface RecommenationPlacesJpaRepository extends JpaRepository<PlaceModel, Long> {
    Optional<List<PlaceModel>> findByCity(String city);
}
