package src.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Place;
import src.domain.repository.RecommendationPlacesRepository;
import src.domain.usecase.PlaceRegistryUseCase;

@Service
public class PlaceRegistryService implements PlaceRegistryUseCase {

    @Autowired
    private RecommendationPlacesRepository repository;

    @Override
    public Long registry(Place placeDomain) {

        var savedPlace = repository.savePlace(placeDomain);

        return savedPlace.getId();
    }
}
