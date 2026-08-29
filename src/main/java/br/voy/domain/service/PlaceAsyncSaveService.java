package br.voy.domain.service;

import br.voy.domain.entity.Place;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceAsyncSaveService {

    private static final Logger logger = LoggerFactory.getLogger(PlaceAsyncSaveService.class);

    private final PlacePersistService persistService;

    @Async
    public void savePlacesAsync(List<Place> places) {
        if (places == null || places.isEmpty()) {
            logger.warn("No places to save asynchronously");
            return;
        }

        logger.info("Starting asynchronous save of {} places", places.size());

        for (Place place : places) {
            try {
                persistService.saveIfAbsent(place);
            } catch (Exception e) {
                logger.error("Error saving place {}: {}", place.getName(), e.getMessage(), e);
            }
        }

        logger.info("Completed asynchronous save of {} places", places.size());
    }
}
