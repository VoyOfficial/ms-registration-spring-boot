package src.domain.service;

import com.google.maps.model.PlaceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.ports.PlacesApiPort;
import src.domain.usecase.GetPlaceDetailsUseCase;

@Service
public class GetPlaceDetailsService implements GetPlaceDetailsUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PlacesApiPort placesApiPort;

    @Override
    public PlaceDetails getPlaceDetails(
            String placeId
    ) {

        logger.info("GET PLACES DETAILS SERVICE - GET PLACE DETAILS START - Place Id: {}", placeId);

        var placeDetails = placesApiPort.getPlaceDetails(placeId);

        logger.info("GET PLACES DETAILS SERVICE - GET PLACE DETAILS FINISH - Place Details: {}", placeDetails);

        return placeDetails;

    }

}
