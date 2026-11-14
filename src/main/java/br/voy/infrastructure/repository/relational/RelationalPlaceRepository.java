package br.voy.infrastructure.repository.relational;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.utils.BoundingBox;
import br.voy.infrastructure.model.PlaceModel;
import br.voy.infrastructure.model.PlacePhotoModel;
import br.voy.infrastructure.repository.jpa.PlaceJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoJpaRepository;
import br.voy.infrastructure.repository.jpa.PlacePhotoRef;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RelationalPlaceRepository implements PlaceRepository {

    /** Stored bytea ceiling. Larger rows are not loaded into a list response. */
    static final int MAX_LIST_PHOTO_BYTES = 150_000;

    /**
     * Largest base64 payload a row within {@link #MAX_LIST_PHOTO_BYTES} can produce. Raw image
     * bytes expand by 4/3; legacy rows that already store base64 text stay at or below the stored
     * size. The list keeps a photo only when both checks agree.
     */
    static final int MAX_LIST_PHOTO_CHARS = ((MAX_LIST_PHOTO_BYTES + 2) / 3) * 4;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private PlaceJpaRepository placeJpaRepository;

    @Autowired private PlacePhotoJpaRepository placePhotoJpaRepository;

    @PersistenceContext private EntityManager entityManager;

    @Override
    @Transactional
    public Place savePlace(Place placeDomain) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE PLACE - Place: {}", placeDomain.getName());

        var placeModel = new PlaceModel(placeDomain);

        placeModel = placeJpaRepository.save(placeModel);

        if (placeDomain.getPhotos() != null && !placeDomain.getPhotos().isEmpty()) {
            List<PlacePhotoModel> placePhotoModelList = new ArrayList<>();
            for (PlacePhoto photo : placeDomain.getPhotos()) {
                PlacePhotoModel photoModel = new PlacePhotoModel(photo);
                photoModel.setPlace(placeModel);
                placePhotoModelList.add(photoModel);
            }

            placePhotoJpaRepository.saveAll(placePhotoModelList);
        }

        return placeModel.toDomain();
    }

    @Override
    public PlacePhoto savePlacePhoto(Long placeId, PlacePhoto placePhoto) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE PLACE PHOTO - Place ID: {}", placeId);

        var placePhotoModel = new PlacePhotoModel(placePhoto);
        placePhotoModel.setPlace(PlaceModel.builder().id(placeId).build());
        placePhotoModel = placePhotoJpaRepository.save(placePhotoModel);

        return placePhotoModel.toDomain();
    }

    @Override
    public List<PlacePhoto> saveAllPlacePhoto(Long placeId, List<PlacePhoto> placePhotos) {
        logger.info("RELATIONAL PLACE REPOSITORY - SAVE ALL PLACE PHOTOS - Place ID: {}", placeId);

        PlaceModel placeRef = PlaceModel.builder().id(placeId).build();
        List<PlacePhotoModel> placePhotoModelList = new ArrayList<>();
        for (PlacePhoto placePhoto : placePhotos) {
            PlacePhotoModel placePhotoModel = new PlacePhotoModel(placePhoto);
            placePhotoModel.setPlace(placeRef);
            placePhotoModelList.add(placePhotoModel);
        }

        placePhotoModelList = placePhotoJpaRepository.saveAll(placePhotoModelList);

        List<PlacePhoto> placePhotoList = new ArrayList<>();
        for (PlacePhotoModel placePhotoModel : placePhotoModelList) {
            placePhotoList.add(placePhotoModel.toDomain());
        }

        return placePhotoList;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Place> findPlaceById(Long placeId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND BY ID - Place ID: {}", placeId);

        var optionalPlaceModel = placeJpaRepository.findById(placeId);

        if (optionalPlaceModel.isPresent()) {

            var placeModel = optionalPlaceModel.get();

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND BY ID - ID: {}", placeId);

            return Optional.of(placeModel.toDomain());
        }

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FIND BY ID - PLACE NOT FOUND - ID : {}", placeId);

        return Optional.empty();
    }

    @Override
    public List<Place> findPlacesByIds(List<Long> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            return List.of();
        }

        List<Place> places = new ArrayList<>();
        for (PlaceModel placeModel : placeJpaRepository.findAllById(placeIds)) {
            places.add(placeModel.toDomain());
        }
        return places;
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Optional<List<PlacePhoto>> findAllPlacePhotoById(Long placeId) {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        List<PlacePhotoModel> placePhotoModelList = placePhotoJpaRepository.findByPlaceId(placeId);

        if (!placePhotoModelList.isEmpty()) {

            logger.info("RELATIONAL PLACE REPOSITORY - FOUND PLACE PHOTO BY ID");

            placePhotoModelList = placePhotoJpaRepository.saveAll(placePhotoModelList);

            List<PlacePhoto> placePhotoList = new ArrayList<>();
            for (PlacePhotoModel placePhotoModel : placePhotoModelList) {
                placePhotoList.add(placePhotoModel.toDomain());
            }

            return Optional.of(placePhotoList);
        }

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACE PHOTO BY ID");

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Optional<Place> findPlaceByGooglePlaceId(String googlePlaceId) {

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FIND BY GOOGLE PLACE ID - Google Place ID: {}",
                googlePlaceId);

        var optionalPlaceModel = placeJpaRepository.findByGooglePlaceId(googlePlaceId);

        if (optionalPlaceModel.isPresent()) {

            var placeModel = optionalPlaceModel.get();

            logger.info(
                    "RELATIONAL PLACE REPOSITORY - FOUND BY GOOGLE PLACE ID - Google Place ID: {}",
                    googlePlaceId);

            return Optional.of(placeModel.toDomain());
        }

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FIND BY GOOGLE PLACE ID - PLACE NOT FOUND - Google Place ID : {}",
                googlePlaceId);

        return Optional.empty();
    }

    @Override
    public Optional<List<Place>> findPlacesWithinBoundingBox(BoundingBox boundingBox) {

        var placeModelList =
                placeJpaRepository.findByBoundingBox(
                        boundingBox.getMinLat(),
                        boundingBox.getMaxLat(),
                        boundingBox.getMinLon(),
                        boundingBox.getMaxLon());

        return Optional.of(toListWithPrincipalPhoto(placeModelList));
    }

    @Override
    public List<Place> findNearbyPlacesByCoordinates(
            double latitude, double longitude, int radiusInMeters) {

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FIND NEARBY PLACES BY COORDINATES - Lat: {}, Lon: {}, Radius: {}m",
                latitude,
                longitude,
                radiusInMeters);

        var placeModelList =
                placeJpaRepository.findNearbyPlacesByCoordinates(
                        latitude, longitude, radiusInMeters);

        List<Place> placesDomain = toListWithPrincipalPhoto(placeModelList);

        logger.info("RELATIONAL PLACE REPOSITORY - FOUND {} NEARBY PLACES", placesDomain.size());

        return placesDomain;
    }

    @Override
    public List<Place> findPlacesWithMissingCoordinates() {

        logger.info("RELATIONAL PLACE REPOSITORY - FIND PLACES WITH MISSING COORDINATES");

        var placeModelList = placeJpaRepository.findPlacesWithMissingCoordinates();

        List<Place> placesDomain = new ArrayList<>();

        for (PlaceModel placeModel : placeModelList) {
            placesDomain.add(placeModel.toDomain());
        }

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FOUND {} PLACES WITH MISSING COORDINATES",
                placesDomain.size());

        return placesDomain;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Place> findPlacesByNameContaining(String placeName) {

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FIND PLACES BY NAME CONTAINING - name: {}",
                placeName);

        if (placeName == null || placeName.isBlank()) {
            return List.of();
        }

        var placeModelList = placeJpaRepository.findByNameContainingIgnoreCase(placeName.trim());

        List<Place> placesDomain = toListWithPrincipalPhoto(placeModelList);

        logger.info(
                "RELATIONAL PLACE REPOSITORY - FOUND {} PLACES BY NAME CONTAINING",
                placesDomain.size());

        return placesDomain;
    }

    private List<Place> toListWithPrincipalPhoto(List<PlaceModel> placeModels) {
        Map<Long, PlacePhoto> principalPhotos = loadPrincipalPhotos(placeModels);
        List<Place> places = new ArrayList<>();
        for (PlaceModel placeModel : placeModels) {
            PlacePhoto principal = principalPhotos.get(placeModel.getId());
            if (principal == null) {
                places.add(placeModel.toListDomain());
            } else {
                places.add(placeModel.toListDomain(List.of(principal)));
            }
        }
        return places;
    }

    private Map<Long, PlacePhoto> loadPrincipalPhotos(List<PlaceModel> placeModels) {
        List<Long> placeIds = new ArrayList<>();
        for (PlaceModel placeModel : placeModels) {
            if (placeModel.getId() != null) {
                placeIds.add(placeModel.getId());
            }
        }
        if (placeIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, Long> earliest = new HashMap<>();
        Map<Long, Long> principalMatch = new HashMap<>();
        for (PlacePhotoRef row :
                placePhotoJpaRepository.findPhotoRefsByPlaceIds(placeIds, MAX_LIST_PHOTO_BYTES)) {
            Long photoId = row.getId();
            Long placeId = row.getPlaceId();
            String reference = row.getPhotoReference();
            String principal = row.getPrincipalPhoto();
            if (photoId == null || placeId == null) {
                continue;
            }
            earliest.merge(placeId, photoId, Math::min);
            if (principal != null && !principal.isBlank() && principal.equals(reference)) {
                principalMatch.putIfAbsent(placeId, photoId);
            }
        }

        Collection<Long> chosenIds = new ArrayList<>();
        for (Long placeId : placeIds) {
            Long photoId = principalMatch.get(placeId);
            if (photoId == null) {
                photoId = earliest.get(placeId);
            }
            if (photoId != null) {
                chosenIds.add(photoId);
            }
        }
        if (chosenIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, PlacePhoto> byPlaceId = new HashMap<>();
        for (PlacePhotoModel photo : placePhotoJpaRepository.findAllById(chosenIds)) {
            PlacePhoto domain = photo.toDomain();
            String encoded = domain.getImageBase64();
            if (encoded != null && encoded.length() > MAX_LIST_PHOTO_CHARS) {
                continue;
            }
            Long placeId = domain.getPlaceId();
            if (placeId != null) {
                byPlaceId.putIfAbsent(placeId, domain);
            }
        }
        return byPlaceId;
    }
}
