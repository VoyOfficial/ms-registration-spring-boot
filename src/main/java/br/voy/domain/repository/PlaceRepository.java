package br.voy.domain.repository;

import br.voy.domain.entity.Place;
import br.voy.domain.utils.BoundingBox;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Place savePlace(Place placeDomain);

    Optional<Place> findById(Long id);

    Optional<List<Place>> findByCity(String city);

    Optional<Place> findPlaceByGooglePlaceId(String googlePlaceId);

    Optional<List<Place>> findPlacesWithinBoundingBox(BoundingBox boundingBox);

}
