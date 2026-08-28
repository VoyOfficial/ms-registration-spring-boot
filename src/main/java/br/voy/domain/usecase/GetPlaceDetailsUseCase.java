package br.voy.domain.usecase;

import br.voy.domain.entity.PlaceDetails;

public interface GetPlaceDetailsUseCase {

    PlaceDetails getPlaceDetails(String placeId);
}
