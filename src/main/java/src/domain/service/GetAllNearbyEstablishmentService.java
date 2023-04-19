package src.domain.service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Location;
import src.domain.ports.EstablishmentLocationPort;
import src.domain.usecase.GetEstablishmentUseCase;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GetAllNearbyEstablishmentService implements GetEstablishmentUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    EstablishmentLocationPort establishmentLocationPort;

    @Override
    public PlacesSearchResponse getNearbyEstablishments(
            Location location,
            Integer radius,
            String placeType
    ) throws IOException, InterruptedException, ApiException {

        logger.info("GET ALL NEARBY ESTABLISHMENT SERVICE - GET NEARBY ESTABLISHMENTS - Location: {}, Radius: {}, PlaceType: {}", location, radius, placeType);

        var establishments = establishmentLocationPort.getNearbyPlaces(location, radius, placeType);

        logger.info("GET ALL NEARBY ESTABLISHMENT SERVICE - GET NEARBY ESTABLISHMENTS - Establishments: {}", establishments);

        return establishments;

    }

}
