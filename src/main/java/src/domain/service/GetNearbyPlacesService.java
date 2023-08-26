package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.Place;
import src.domain.ports.PlacesApiPort;
import src.domain.usecase.GetNearbyPlacesUseCase;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlacesApiPort placesApiPort;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        var placesSearchResponse = placesApiPort.getNearbyPlaces(coordinates, radius, placeType, nextPageToken);

        var placesEntities = Arrays
                .stream(placesSearchResponse.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        var nearbyPlaces = new NearbyPlaces(placesEntities, placesSearchResponse.nextPageToken);

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

}
