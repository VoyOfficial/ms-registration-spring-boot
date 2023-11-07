package src.domain.repository;

import src.domain.entity.Place;

import java.util.List;

public interface RecommendationPlacesRepository {

    List<Place> getRecommendationPlaces();

    Place savePlace(Place placeDomain);
}
