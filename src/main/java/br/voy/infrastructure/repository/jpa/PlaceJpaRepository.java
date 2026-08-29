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

    @Query(
            value =
                    "SELECT * FROM registration.place p "
                            + "WHERE p.latitude != 0 AND p.longitude != 0 "
                            + "AND (6371000 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * "
                            + "cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * "
                            + "sin(radians(p.latitude)))) <= :radiusInMeters "
                            + "ORDER BY (6371000 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * "
                            + "cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * "
                            + "sin(radians(p.latitude))))",
            nativeQuery = true)
    List<PlaceModel> findNearbyPlacesByCoordinates(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInMeters") int radiusInMeters);

    @Query(
            "SELECT p FROM PlaceModel p WHERE (p.latitude = 0 OR p.latitude IS NULL) AND (p.longitude = 0 OR p.longitude IS NULL) AND p.googlePlaceId IS NOT NULL")
    List<PlaceModel> findPlacesWithMissingCoordinates();
}
