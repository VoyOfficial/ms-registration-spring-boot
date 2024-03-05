package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.ports.GooglePlacesPort;
import src.domain.usecase.GetNearbyPlacesUseCase;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GooglePlacesPort googlePlacesPort;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        var nearbyPlaces = googlePlacesPort.getNearbyPlaces(coordinates, radius, placeType, nextPageToken);

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Nearby Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

}
