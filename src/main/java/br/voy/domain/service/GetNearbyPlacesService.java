package br.voy.domain.service;

import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.ports.GooglePlacesPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
