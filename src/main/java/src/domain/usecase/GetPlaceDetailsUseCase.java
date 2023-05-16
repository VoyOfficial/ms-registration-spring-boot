package src.domain.usecase;

import com.google.maps.model.PlaceDetails;

public interface GetPlaceDetailsUseCase {

    PlaceDetails getPlaceDetails(
            String placeId
    );

}
