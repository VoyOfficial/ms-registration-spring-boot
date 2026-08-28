package br.voy.domain.service;

import br.voy.domain.entity.Place;
import br.voy.domain.exception.PlaceAlreadyExistsException;
import br.voy.domain.repository.PlaceRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceAsyncSaveService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private PlaceRepository placeRepository;

    @Async("taskExecutor")
    public void savePlacesAsync(List<Place> places) {
        logger.info(
                "PLACE ASYNC SAVE SERVICE - ASYNC SAVE START - Places count: {}", places.size());

        int savedCount = 0;
        int skippedCount = 0;
        int errorCount = 0;

        for (Place place : places) {
            try {
                savePlaceIndividually(place);
                savedCount++;
                logger.info(
                        "PLACE ASYNC SAVE SERVICE - PLACE SAVED - Place: {} (ID: {})",
                        place.getName(),
                        place.getGooglePlaceId());
            } catch (PlaceAlreadyExistsException e) {
                skippedCount++;
                logger.debug(
                        "PLACE ASYNC SAVE SERVICE - PLACE ALREADY EXISTS - Place: {} (ID: {})",
                        place.getName(),
                        place.getGooglePlaceId());
            } catch (Exception e) {
                errorCount++;
                logger.error(
                        "PLACE ASYNC SAVE SERVICE - ERROR SAVING PLACE - Place: {}, ID: {}, Error: {}",
                        place.getName(),
                        place.getGooglePlaceId(),
                        e.getMessage(),
                        e);
            }
        }

        logger.info(
                "PLACE ASYNC SAVE SERVICE - ASYNC SAVE FINISH - Total: {}, Saved: {}, Skipped: {}, Errors: {}",
                places.size(),
                savedCount,
                skippedCount,
                errorCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void savePlaceIndividually(Place place) {
        var existingPlace = placeRepository.findPlaceByGooglePlaceId(place.getGooglePlaceId());

        if (existingPlace.isEmpty()) {
            // Ensure required fields have default values if null
            // Check if any required NOT NULL database fields are missing
            boolean needsDefaults =
                    place.getContact() == null
                            || place.getContact().isEmpty()
                            || place.getDistanceOfLocal() == null;

            if (needsDefaults) {
                place =
                        Place.builder()
                                .id(place.getId())
                                .googlePlaceId(place.getGooglePlaceId())
                                .name(place.getName())
                                .about(place.getAbout() != null ? place.getAbout() : "")
                                .contact(
                                        place.getContact() != null && !place.getContact().isEmpty()
                                                ? place.getContact()
                                                : "")
                                .address(place.getAddress() != null ? place.getAddress() : "")
                                .city(place.getCity())
                                .rating(place.getRating() != null ? place.getRating() : 0.0f)
                                .userRatingsTotal(
                                        place.getUserRatingsTotal() != null
                                                ? place.getUserRatingsTotal()
                                                : 0)
                                .principalPhoto(
                                        place.getPrincipalPhoto() != null
                                                ? place.getPrincipalPhoto()
                                                : "")
                                .principalPhotoUrl(place.getPrincipalPhotoUrl())
                                .status(place.isStatus())
                                .ranking(place.getRanking())
                                .startRecommendation(place.getStartRecommendation())
                                .endRecommendation(place.getEndRecommendation())
                                .createdAt(place.getCreatedAt())
                                .lastCancel(place.getLastCancel())
                                .distanceOfLocal(
                                        place.getDistanceOfLocal() != null
                                                ? place.getDistanceOfLocal()
                                                : 0.0f)
                                .latitude(place.getLatitude())
                                .longitude(place.getLongitude())
                                .photos(place.getPhotos())
                                .build();
            }

            placeRepository.savePlace(place);
        } else {
            throw new PlaceAlreadyExistsException("Place already exists");
        }
    }
}
