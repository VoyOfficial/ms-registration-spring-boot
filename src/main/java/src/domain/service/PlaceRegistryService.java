package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Place;
import src.domain.entity.PlaceDetails;
import src.domain.exception.CityDifferentPlaceRecommendationException;
import src.domain.exception.PlaceAlreadyExistsException;
import src.domain.ports.GooglePlacesPort;
import src.domain.repository.PlaceRepository;
import src.domain.usecase.PlaceRegistryUseCase;

import java.time.LocalDate;

@Service
public class PlaceRegistryService implements PlaceRegistryUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceRepository repository;

    @Autowired
    private GooglePlacesPort googlePlacesPort;

    @Override
    public Long registry(Place placeDomain) {

        logger.info("PLACE REGISTRY SERVICE - REGISTRY - Place: {}", placeDomain.getName());

        var recommendedPlace = processingDataPlace(placeDomain);

        verifyIfPlaceAlreadyExistsInDatabase(recommendedPlace.getGooglePlaceId());

        var statusRecommendedPlace = true;
        var startRecommendation = LocalDate.now();
        var createdAt = LocalDate.now();
        var endRecommendation = startRecommendation.plusMonths(1);

        recommendedPlace.setStatus(statusRecommendedPlace);
        recommendedPlace.setStartRecommendation(startRecommendation);
        recommendedPlace.setCreatedAt(createdAt);
        recommendedPlace.setEndRecommendation(endRecommendation);

        Place savedPlace = repository.savePlace(recommendedPlace);

        logger.info("PLACE REGISTRY SERVICE - REGISTRY - Registered Recommended Place: {}", savedPlace.getName());

        return savedPlace.getId();

    }

    private Place processingDataPlace(Place placeDomain) {

        logger.info("PLACE REGISTRY SERVICE - Starting processing Data of Place");

        PlaceDetails placeDetails = googlePlacesPort.getPlaceFromText(placeDomain.getName(), placeDomain.getCity());

        String city = extractCityOfPlaceDetails(placeDomain.getCity(), placeDetails.getAddress());

        logger.info("PLACE REGISTRY SERVICE - Ending processing Data of Place");

        return Place
                .builder()
                .googlePlaceId(placeDetails.getGooglePlaceId())
                .name(placeDetails.getName())
                .contact(placeDetails.getContact())
                .address(placeDetails.getAddress())
                .city(city)
                .ranking(placeDomain.getRanking())
//                .latitude(placeDetails.geometry.location.lat) TODO Verificar necessidade
//                .longitude(placeDetails.geometry.location.lng)
                .build();
    }


    private String extractCityOfPlaceDetails(String placeDomainCity, String googlePlaceAddress) {

        // The number 1 in split(", ") is used to retrieve the second part of the split string,
        // which should contain the city and state.
        // Then, the number 0 in split(" - ") is used to extract only the city.
        String extractedCity = googlePlaceAddress.split(", ")[1].split(" - ")[0].trim();

        if (!extractedCity.contentEquals(placeDomainCity)) {
            throw new CityDifferentPlaceRecommendationException();
        }

        return extractedCity;

    }

    private void verifyIfPlaceAlreadyExistsInDatabase(String googlePlaceId) {

        logger.info("PLACE REGISTRY SERVICE - Verify If Place Already Exists In Database");

        if (repository.findPlaceByGooglePlaceId(googlePlaceId).isPresent()) {

            logger.info("PLACE REGISTRY SERVICE - Place Already Exists In Database");
            throw new PlaceAlreadyExistsException();

        }

    }

}
