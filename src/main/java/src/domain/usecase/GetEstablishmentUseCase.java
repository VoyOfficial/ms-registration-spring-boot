package src.domain.usecase;

import com.google.maps.model.PlacesSearchResponse;
import src.domain.entity.Location;

public interface GetEstablishmentUseCase {

    PlacesSearchResponse getNearbyEstablishments(
            Location location,
            Integer radius,
            String placeType
    );

}
