package src.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Place;
import src.domain.repository.PlaceRepository;
import src.domain.usecase.GetPlaceDetailsUseCase;
import src.domain.usecase.PlaceRegistryUseCase;

@Service
public class PlaceRegistryService implements PlaceRegistryUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceRepository repository;

    @Autowired
    private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @Override
    public Long registry(Place placeDomain) {

        var savedPlace = repository.savePlace(mapperPlace(placeDomain));

        logger.info("PLACE REGISTRY SERVICE - REGISTRY - Place: {}", savedPlace.getName());

        return savedPlace.getId();

    }

    Place mapperPlace(Place placeDomain){
        //metodo para obter os detalhes do lugar
        var placeDetails = getPlaceDetailsUseCase.getPlaceDetails(placeDomain.getGooglePlaceId());
        return Place.builder()
                .googlePlaceId(placeDetails.getGooglePlaceId())
                .name(placeDetails.getName())
                .contact(placeDetails.getContact())
                .address(placeDetails.getAddress())
                .city(placeDetails.getCity())
                .status(placeDomain.isStatus())
                .ranking(placeDomain.getRanking())
                .startRecommendation(placeDomain.getStartRecommendation())
                .endRecommendation(placeDomain.getEndRecommendation())
                .createdDate(placeDomain.getCreatedDate())
                .latitude(placeDetails.getLatitude())
                .longitude(placeDetails.getLongitude())
                .build();
    }
}
