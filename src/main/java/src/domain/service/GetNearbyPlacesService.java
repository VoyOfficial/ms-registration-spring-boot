package src.domain.service;

import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Coordinates;
import src.domain.ports.PlacesApiPort;
import src.domain.usecase.GetNearbyPlacesUseCase;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlacesApiPort placesApiPort;

    @Override
    public PlacesSearchResponse getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        var nearbyPlaces = placesApiPort.getNearbyPlaces(coordinates, radius, placeType, nextPageToken);

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

}
