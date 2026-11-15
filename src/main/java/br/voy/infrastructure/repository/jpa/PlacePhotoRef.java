package br.voy.infrastructure.repository.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Photo identity for the nearby list, without the stored image bytes. */
@Getter
@AllArgsConstructor
public class PlacePhotoRef {

    private final Long id;
    private final Long placeId;
    private final String photoReference;
    private final String principalPhoto;
}
