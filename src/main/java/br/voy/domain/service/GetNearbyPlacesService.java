package br.voy.domain.service;

import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GooglePlacesPort googlePlacesPort;

    @Autowired
    PlaceRepository placeRepository;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        var nearbyPlaces = googlePlacesPort.getNearbyPlaces(coordinates, radius, placeType, nextPageToken);

        // Save places and photos to database
        List<Place> savedPlaces = new ArrayList<>();
        for (Place place : nearbyPlaces.getPlaces()) {
            // Check if place already exists
            var existingPlace = placeRepository.findPlaceByGooglePlaceId(place.getGooglePlaceId());

            if (existingPlace.isEmpty()) {
                // Place doesn't exist, save it with photos
                if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
                    logger.info("GET NEARBY PLACES SERVICE - SAVING PLACE WITH PHOTOS - Place: {}, Photos: {}", place.getName(), place.getPhotos().size());
                    Place savedPlace = placeRepository.savePlace(place);
                    savedPlaces.add(savedPlace);
                } else {
                    logger.info("GET NEARBY PLACES SERVICE - SAVING PLACE WITHOUT PHOTOS - Place: {}", place.getName());
                    Place savedPlace = placeRepository.savePlace(place);
                    savedPlaces.add(savedPlace);
                }
            } else {
                logger.info("GET NEARBY PLACES SERVICE - PLACE ALREADY EXISTS - Place: {}", place.getName());
                savedPlaces.add(existingPlace.get());
            }
        }

        var result = new NearbyPlaces(savedPlaces, nearbyPlaces.getNextTokenPage());

        logger.info("GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Nearby Places: {}", result);

        return result;

    }

}
