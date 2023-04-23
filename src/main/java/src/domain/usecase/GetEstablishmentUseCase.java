package src.domain.usecase;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Coordinates;

public interface GetEstablishmentUseCase {

    PlacesSearchResponse getNearbyEstablishments(
            Coordinates coordinates,
            Integer radius,
            String placeType
    );

}
