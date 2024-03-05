package src.infrastructure.repository.relational;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import src.domain.entity.Place;
import src.domain.repository.PlaceRepository;
import src.infrastructure.model.PlaceModel;
import src.infrastructure.repository.jpa.PlaceJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RelationalPlaceRepository implements PlaceRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceJpaRepository jpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Place savePlace(Place placeDomain) {

        logger.info("RELATIONAL PLACE REPOSITORY - SAVE PLACE - Place: {}", placeDomain.getName());

        var placeModel = new PlaceModel(placeDomain);

        placeModel = jpaRepository.save(placeModel);

        return placeModel.toDomain();
    }

    @Override
    public Optional<Place> findById(Long placeId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - Place ID: {}", placeId);

        var optionalPlaceModel = jpaRepository.findById(placeId);

        if(optionalPlaceModel.isPresent()){

            var userModel = optionalPlaceModel.get();

            logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - ID: {}", placeId);

            return Optional.of(userModel.toDomain());

        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - PLACE NOT FOUND - ID : {}", placeId);

        return Optional.empty();

    }

    @Override
    public Optional<List<Place>> findByCity(String city) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY CITY - Place: {}", city);

        var optionalPlaceModel = jpaRepository.findByCity(city);

        if(optionalPlaceModel.isPresent()) {

            var placesModel = optionalPlaceModel.get();
            List<Place> placesDomain = new ArrayList<>();

            for(PlaceModel placeModel : placesModel) {
                placesDomain.add(placeModel.toDomain());
            }

            return Optional.of(placesDomain);

        }

        return Optional.empty();

    }

    @Override
    public boolean findByGooglePlaceId(String googlePlaceId) {
            try {
                return jpaRepository.findByGooglePlaceId(googlePlaceId) != null;
            } catch (NoResultException e) {
                logger.info(e.getMessage());
                return false; // No matching entity found
            }
    }
}
