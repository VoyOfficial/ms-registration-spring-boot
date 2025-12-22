package br.voy.infrastructure.repository.relational;

import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.utils.BoundingBox;
import br.voy.infrastructure.model.PlaceModel;
import br.voy.infrastructure.model.PlacePhotoModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import br.voy.domain.entity.Place;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RelationalPlaceRepository implements PlaceRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlacePhotoJpaRepository placePhotoJpaRepository;



    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Place savePlace(Place placeDomain) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE PLACE - Place: {}", placeDomain.getName());

        var placeModel = new PlaceModel(placeDomain);

        placeModel = placeJpaRepository.save(placeModel);

        // Save photos if they exist
        if (placeDomain.getPhotos() != null && !placeDomain.getPhotos().isEmpty()) {
            List<PlacePhotoModel> placePhotoModelList = new ArrayList<>();
            for(PlacePhoto photo : placeDomain.getPhotos()) {
                PlacePhotoModel photoModel = new PlacePhotoModel();
                photoModel.setPhotoReference(photo.getPhotoReference());
                photoModel.setPhotoUrl(photo.getPhotoUrl());
                photoModel.setHeight(photo.getHeight());
                photoModel.setWidth(photo.getWidth());
                photoModel.setHtmlAttributions(photo.getHtmlAttributions());
                if (photo.getImageBase64() != null) {
                    photoModel.setImageBase64(photo.getImageBase64().getBytes());
                }
                photoModel.setPlace(placeModel);
                placePhotoModelList.add(photoModel);
            }

            placePhotoJpaRepository.saveAll(placePhotoModelList);
        }

        return placeModel.toDomain();
    }

    @Override
    public PlacePhoto savePlacePhoto(PlaceModel place, PlacePhoto placePhoto) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE PLACE PHOTO - Place: {}", place.getName());

        var placePhotoModel = new PlacePhotoModel(placePhoto);

        placePhotoModel = placePhotoJpaRepository.save(placePhotoModel);

        return placePhotoModel.toDomain();
    }

    @Override
    public List<PlacePhoto> saveAllPlacePhoto(PlaceModel place, List<PlacePhoto> placePhotos) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE ALL PLACE PHOTOS - Place: {}", place.getName());

        List<PlacePhotoModel> placePhotoModelList = new ArrayList<>();
        for(PlacePhoto placePhoto: placePhotos) {
            placePhotoModelList.add(new PlacePhotoModel(placePhoto));
        }

        placePhotoModelList = placePhotoJpaRepository.saveAll(placePhotoModelList);

        List<PlacePhoto> placePhotoList = new ArrayList<>();
        for(PlacePhotoModel placePhotoModel : placePhotoModelList) {
            placePhotoList.add(placePhotoModel.toDomain());
        }

        return placePhotoList;
    }

    @Override
    public Optional<Place> findPlaceById(Long placeId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - Place ID: {}", placeId);

        var optionalPlaceModel = placeJpaRepository.findById(placeId);

        if (optionalPlaceModel.isPresent()) {

            var placeModel = optionalPlaceModel.get();

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND BY ID - ID: {}", placeId);

            return Optional.of(placeModel.toDomain());

        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - PLACE NOT FOUND - ID : {}", placeId);

        return Optional.empty();

    }

    @Override
    public Optional<PlacePhoto> findPlacePhotoById(Long photoId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        var optionalPlacePhotoModel = placePhotoJpaRepository.findById(photoId);

        if (optionalPlacePhotoModel.isPresent()) {

            var placePhotoModel = optionalPlacePhotoModel.get();

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND PLACE PHOTO BY ID");

            return Optional.of(placePhotoModel.toDomain());
        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        return Optional.empty();

    }

    @Override
    public Optional<List<PlacePhoto>> findAllPlacePhotoById(Long placeId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        List<PlacePhotoModel> placePhotoModelList = placePhotoJpaRepository.findByPlaceId(placeId);

        if (!placePhotoModelList.isEmpty()) {

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND PLACE PHOTO BY ID");

            placePhotoModelList = placePhotoJpaRepository.saveAll(placePhotoModelList);

            List<PlacePhoto> placePhotoList = new ArrayList<>();
            for(PlacePhotoModel placePhotoModel : placePhotoModelList) {
                placePhotoList.add(placePhotoModel.toDomain());
            }

            return Optional.of(placePhotoList);
        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        return Optional.empty();
    }

    @Override
    public Optional<List<Place>> findPlaceByCity(String city) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY CITY - Place: {}", city);

        var placesModel = placeJpaRepository.findByCity(city);

        if (!placesModel.isEmpty()) {

            List<Place> placesDomain = new ArrayList<>();

            for (PlaceModel placeModel : placesModel) {
                placesDomain.add(placeModel.toDomain());
            }

            return Optional.of(placesDomain);

        }

        return Optional.empty();

    }

    @Override
    public Optional<Place> findPlaceByGooglePlaceId(String googlePlaceId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY GOOGLE PLACE ID - Google Place ID: {}", googlePlaceId);

        var optionalPlaceModel = placeJpaRepository.findByGooglePlaceId(googlePlaceId);

        if (optionalPlaceModel.isPresent()) {

            var placeModel = optionalPlaceModel.get();

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND BY GOOGLE PLACE ID - Google Place ID: {}", googlePlaceId);

            return Optional.of(placeModel.toDomain());

        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY GOOGLE PLACE ID - PLACE NOT FOUND - Google Place ID : {}", googlePlaceId);

        return Optional.empty();

    }

    @Override
    public Optional<List<Place>> findPlacesWithinBoundingBox(BoundingBox boundingBox) {

        var placeModelList = placeJpaRepository.findByBoundingBox(boundingBox.getMinLat(),
                boundingBox.getMaxLat(),
                boundingBox.getMinLon(),
                boundingBox.getMaxLon());

        List<Place> placesDomain = new ArrayList<>();

        for (PlaceModel placeModel : placeModelList) {
            placesDomain.add(placeModel.toDomain());
        }

        return Optional.of(placesDomain);

    }

}