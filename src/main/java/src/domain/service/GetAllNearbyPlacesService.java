package src.domain.service;

import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Coordinates;
import src.domain.ports.PlacesApiPort;
import src.domain.usecase.GetEstablishmentUseCase;

@Service
public class GetAllNearbyPlacesService implements GetEstablishmentUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlacesApiPort placesApiPort;

    @Override
    public PlacesSearchResponse getNearbyEstablishments(
            Coordinates coordinates,
            Integer radius,
            String placeType
    ) {

        logger.info("GET ALL NEARBY ESTABLISHMENT SERVICE - GET NEARBY ESTABLISHMENTS - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        var establishments = placesApiPort.getNearbyPlaces(coordinates, radius, placeType);

        logger.info("GET ALL NEARBY ESTABLISHMENT SERVICE - GET NEARBY ESTABLISHMENTS - Establishments: {}", establishments);

        return establishments;

    }

}
