package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.PlaceModel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceJpaRepository extends JpaRepository<PlaceModel, Long> {

    Optional<PlaceModel> findById(Long placeId);

    List<PlaceModel> findByCity(String city);

    Optional<PlaceModel> findByGooglePlaceId(String googlePlaceId);

    @Query(
            "SELECT p FROM PlaceModel p WHERE p.latitude BETWEEN :minLat AND :maxLat AND p.longitude BETWEEN :minLon AND :maxLon")
    List<PlaceModel> findByBoundingBox(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon);
}
