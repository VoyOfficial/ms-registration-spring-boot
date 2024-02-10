package src.domain.usecase;

import src.domain.entity.PlaceDetails;

public interface GetPlaceDetailsUseCase {

    PlaceDetails getPlaceDetails(
            String placeId
    );

}
