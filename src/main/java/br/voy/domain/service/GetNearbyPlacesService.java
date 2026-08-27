package br.voy.domain.service;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired GooglePlacesPort googlePlacesPort;

    @Autowired PlaceRepository placeRepository;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken) {

        logger.info(
                "GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}",
                coordinates,
                radius,
                placeType);

        var nearbyPlaces =
                googlePlacesPort.getNearbyPlaces(coordinates, radius, placeType, nextPageToken);

        savePlacesAsync(nearbyPlaces.getPlaces());

        logger.info(
                "GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Nearby Places: {}",
                nearbyPlaces.getPlaces().size());

        return nearbyPlaces;
    }

    @Async
    public void savePlacesAsync(List<Place> places) {
        logger.info(
                "GET NEARBY PLACES SERVICE - ASYNC SAVE START - Places count: {}", places.size());

        for (Place place : places) {
            try {
                var existingPlace =
                        placeRepository.findPlaceByGooglePlaceId(place.getGooglePlaceId());

                if (existingPlace.isEmpty()) {
                    placeRepository.savePlace(place);
                    logger.debug(
                            "GET NEARBY PLACES SERVICE - PLACE SAVED - Place: {}", place.getName());
                } else {
                    logger.debug(
                            "GET NEARBY PLACES SERVICE - PLACE ALREADY EXISTS - Place: {}",
                            place.getName());
                }
            } catch (Exception e) {
                logger.error(
                        "GET NEARBY PLACES SERVICE - ERROR SAVING PLACE - Place: {}, Error: {}",
                        place.getName(),
                        e.getMessage());
            }
        }

        logger.info(
                "GET NEARBY PLACES SERVICE - ASYNC SAVE FINISH - Places saved/checked: {}",
                places.size());
    }
}
