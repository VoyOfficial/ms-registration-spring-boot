package br.voy.domain.repository;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.utils.BoundingBox;
import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Place savePlace(Place placeDomain);

    PlacePhoto savePlacePhoto(Long placeId, PlacePhoto placePhoto);

    List<PlacePhoto> saveAllPlacePhoto(Long placeId, List<PlacePhoto> placePhoto);

    List<Place> findPlacesByIds(List<Long> placeIds);

    Optional<Place> findPlaceById(Long placeId);

    Optional<PlacePhoto> findPlacePhotoById(Long photoId);

    Optional<List<PlacePhoto>> findAllPlacePhotoById(Long placeId);

    Optional<List<Place>> findPlaceByCity(String city);

    Optional<Place> findPlaceByGooglePlaceId(String googlePlaceId);

    Optional<List<Place>> findPlacesWithinBoundingBox(BoundingBox boundingBox);

    List<Place> findNearbyPlacesByCoordinates(
            double latitude, double longitude, int radiusInMeters);

    List<Place> findPlacesWithMissingCoordinates();
}
