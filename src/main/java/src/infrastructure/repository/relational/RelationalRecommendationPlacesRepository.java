package src.infrastructure.repository.relational;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Place;
import src.domain.repository.RecommendationPlacesRepository;
import src.infrastructure.model.PlaceModel;
import src.infrastructure.repository.jpa.RecommenationPlacesJpaRepository;

import java.util.List;

@Service
public class RelationalRecommendationPlacesRepository implements RecommendationPlacesRepository {

    @Autowired
    private RecommenationPlacesJpaRepository jpaRepository;

    @Override
    public List<Place> getRecommendationPlaces() {
        return null;
    }

    @Override
    public Place savePlace(Place placeDomain) {

        var placeModel = new PlaceModel(placeDomain);

        placeModel = jpaRepository.save(placeModel);

        return placeModel.toDomain();
    }


}
