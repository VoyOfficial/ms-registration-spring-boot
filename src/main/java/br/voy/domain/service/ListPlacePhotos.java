package br.voy.domain.service;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import java.util.List;

final class ListPlacePhotos {

    private ListPlacePhotos() {}

    static List<PlacePhoto> principalOnly(Place place) {
        if (place.getPhotos() == null || place.getPhotos().isEmpty()) {
            return List.of();
        }

        String principalReference = place.getPrincipalPhoto();
        PlacePhoto fallback = null;
        for (PlacePhoto photo : place.getPhotos()) {
            String encoded = photo.getImageBase64();
            if (encoded == null || encoded.isBlank()) {
                continue;
            }
            if (fallback == null) {
                fallback = photo;
            }
            if (principalReference != null
                    && !principalReference.isBlank()
                    && principalReference.equals(photo.getPhotoReference())) {
                return List.of(photo);
            }
        }
        return fallback == null ? List.of() : List.of(fallback);
    }
}
