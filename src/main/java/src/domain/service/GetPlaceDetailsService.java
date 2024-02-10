package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.PlaceDetails;
import src.domain.ports.GooglePlacesPort;
import src.domain.usecase.GetPlaceDetailsUseCase;

@Service
public class GetPlaceDetailsService implements GetPlaceDetailsUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GooglePlacesPort googlePlacesPort;

    @Override
    public PlaceDetails getPlaceDetails(String placeId) {

        logger.info("GET PLACES DETAILS SERVICE - GET PLACE DETAILS START - Place Id: {}", placeId);

        var place = googlePlacesPort.getPlaceDetails(placeId);

        logger.info("GET PLACES DETAILS SERVICE - GET PLACE DETAILS FINISH - Place Details: {}", place);

        return place;

    }

}
