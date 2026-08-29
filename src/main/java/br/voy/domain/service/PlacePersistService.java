package br.voy.domain.service;

import br.voy.domain.entity.Place;
import br.voy.domain.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlacePersistService {

    @Autowired private PlaceRepository placeRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveIfAbsent(Place place) {
        var existingPlace = placeRepository.findPlaceByGooglePlaceId(place.getGooglePlaceId());

        if (existingPlace.isPresent()) {
            return;
        }

        boolean needsDefaults =
                place.getContact() == null
                        || place.getContact().isEmpty()
                        || place.getDistanceOfLocal() == null;

        if (needsDefaults) {
            place =
                    Place.builder()
                            .id(place.getId())
                            .googlePlaceId(place.getGooglePlaceId())
                            .name(place.getName())
                            .about(place.getAbout() != null ? place.getAbout() : "")
                            .contact(
                                    place.getContact() != null && !place.getContact().isEmpty()
                                            ? place.getContact()
                                            : "")
                            .address(place.getAddress() != null ? place.getAddress() : "")
                            .city(place.getCity())
                            .rating(place.getRating() != null ? place.getRating() : 0.0f)
                            .userRatingsTotal(
                                    place.getUserRatingsTotal() != null
                                            ? place.getUserRatingsTotal()
                                            : 0)
                            .principalPhoto(
                                    place.getPrincipalPhoto() != null
                                            ? place.getPrincipalPhoto()
                                            : "")
                            .principalPhotoUrl(place.getPrincipalPhotoUrl())
                            .status(place.isStatus())
                            .ranking(place.getRanking())
                            .startRecommendation(place.getStartRecommendation())
                            .endRecommendation(place.getEndRecommendation())
                            .createdAt(place.getCreatedAt())
                            .lastCancel(place.getLastCancel())
                            .distanceOfLocal(
                                    place.getDistanceOfLocal() != null
                                            ? place.getDistanceOfLocal()
                                            : 0.0f)
                            .latitude(place.getLatitude())
                            .longitude(place.getLongitude())
                            .photos(place.getPhotos())
                            .build();
        }

        placeRepository.savePlace(place);
    }
}
