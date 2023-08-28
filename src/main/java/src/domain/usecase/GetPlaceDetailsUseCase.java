package src.domain.usecase;

import src.domain.entity.Place;

public interface GetPlaceDetailsUseCase {

    Place getPlaceDetails(
            String placeId
    );

}
