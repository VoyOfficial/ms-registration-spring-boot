package src.domain.repository;

import src.domain.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Place savePlace(Place placeDomain);

    Optional<Place> findById(Long id);

    Optional<List<Place>> findByCity(String city);

    boolean findByGooglePlaceId(String googlePlaceId);

}
