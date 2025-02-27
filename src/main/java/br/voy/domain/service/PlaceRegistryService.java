package br.voy.domain.service;

import br.voy.domain.exception.CityDifferentPlaceRecommendationException;
import br.voy.domain.exception.PlaceAlreadyExistsException;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.PlaceRegistryUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.ports.GooglePlacesPort;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String regex = ",\\s*([^,]+)\\s*-\\s*[A-Z]{2},";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(googlePlaceAddress);

        if(!matcher.find()){
            throw new CityDifferentPlaceRecommendationException();
        }

        String extractedCity = matcher.group(1).trim();

        if (!extractedCity.equals(placeDomainCity)) {
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
