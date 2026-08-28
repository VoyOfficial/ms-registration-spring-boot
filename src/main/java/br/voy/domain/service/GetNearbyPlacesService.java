package br.voy.domain.service;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.GetNearbyPlacesUseCase;
import br.voy.domain.utils.PaginationTokenEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${voy.services.places.maxPlaceSizeList}")
    private int pageSize;

    @Autowired GooglePlacesPort googlePlacesPort;

    @Autowired PlaceRepository placeRepository;

    @Autowired PlaceAsyncSaveService placeAsyncSaveService;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken) {

        logger.info(
                "GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}, NextPageToken: {}",
                coordinates,
                radius,
                placeType,
                nextPageToken);

        // Decode pagination state
        PaginationTokenEncoder.PaginationState paginationState;
        try {
            paginationState =
                    nextPageToken != null && !nextPageToken.isEmpty()
                            ? PaginationTokenEncoder.decode(nextPageToken)
                            : new PaginationTokenEncoder.PaginationState(0, new HashSet<>(), null);
        } catch (IllegalArgumentException e) {
            logger.warn(
                    "GET NEARBY PLACES SERVICE - Invalid token, treating as first page: {}",
                    e.getMessage());
            paginationState =
                    new PaginationTokenEncoder.PaginationState(0, new HashSet<>(), nextPageToken);
        }

        int offset = paginationState.getOffset();
        Set<String> shownPlaceIds = new HashSet<>(paginationState.getShownPlaceIds());
        String googleToken = paginationState.getGoogleNextPageToken();

        logger.info(
                "GET NEARBY PLACES SERVICE - Pagination State - Offset: {}, Shown IDs: {}, Google Token: {}",
                offset,
                shownPlaceIds.size(),
                googleToken != null ? "present" : "null");

        // Fetch all database places (we'll paginate from this list)
        List<Place> allDbPlaces =
                placeRepository.findNearbyPlacesByCoordinates(
                        coordinates.getLatitude(), coordinates.getLongitude(), radius);

        logger.info("GET NEARBY PLACES SERVICE - Found {} places in database", allDbPlaces.size());

        // Result list to return
        List<Place> resultPlaces = new ArrayList<>();
        Set<String> newShownPlaceIds = new HashSet<>(shownPlaceIds);

        // Try to fill from database first
        if (!allDbPlaces.isEmpty() && offset < allDbPlaces.size()) {
            logger.info(
                    "GET NEARBY PLACES SERVICE - Fetching from database starting at offset {}",
                    offset);

            List<Place> dbPagePlaces =
                    allDbPlaces.stream()
                            .skip(offset)
                            .filter(place -> !shownPlaceIds.contains(place.getGooglePlaceId()))
                            .limit(pageSize)
                            .map(this::normalizePlace)
                            .collect(Collectors.toList());

            resultPlaces.addAll(dbPagePlaces);
            dbPagePlaces.forEach(place -> newShownPlaceIds.add(place.getGooglePlaceId()));

            logger.info(
                    "GET NEARBY PLACES SERVICE - Got {} places from database (after deduplication)",
                    dbPagePlaces.size());

            // Check if we have a full page from database
            if (resultPlaces.size() >= pageSize) {
                int nextOffset = offset + dbPagePlaces.size();

                // Generate next token for database pagination
                String nextToken =
                        PaginationTokenEncoder.encode(nextOffset, newShownPlaceIds, null);

                logger.info(
                        "GET NEARBY PLACES SERVICE - Returning full page from database. Next offset: {}",
                        nextOffset);
                return new NearbyPlaces(resultPlaces.subList(0, pageSize), nextToken);
            }

            // If database has more results, continue from there in next request
            if (offset + dbPagePlaces.size() < allDbPlaces.size()) {
                int nextOffset = offset + dbPagePlaces.size();
                String nextToken =
                        PaginationTokenEncoder.encode(nextOffset, newShownPlaceIds, null);

                logger.info(
                        "GET NEARBY PLACES SERVICE - Returning {} places from database (partial page). Next offset: {}",
                        resultPlaces.size(),
                        nextOffset);
                return new NearbyPlaces(resultPlaces, nextToken);
            }
        }

        // Database is exhausted or insufficient, fetch from Google API to complete the page
        int placesNeeded = pageSize - resultPlaces.size();

        if (placesNeeded > 0) {
            logger.info(
                    "GET NEARBY PLACES SERVICE - Database exhausted or insufficient. Need {} more places. Fetching from Google API",
                    placesNeeded);

            // Get all database place IDs to filter duplicates
            Set<String> allDbPlaceIds =
                    allDbPlaces.stream().map(Place::getGooglePlaceId).collect(Collectors.toSet());

            // Merge with already shown IDs
            Set<String> allExistingIds = new HashSet<>(allDbPlaceIds);
            allExistingIds.addAll(shownPlaceIds);

            // Fetch from Google API
            NearbyPlaces googleResponse =
                    googlePlacesPort.getNearbyPlaces(coordinates, radius, placeType, googleToken);

            // Save places asynchronously
            if (!googleResponse.getPlaces().isEmpty()) {
                placeAsyncSaveService.savePlacesAsync(googleResponse.getPlaces());
            }

            // Filter and add Google places
            List<Place> filteredGooglePlaces =
                    googleResponse.getPlaces().stream()
                            .filter(place -> !allExistingIds.contains(place.getGooglePlaceId()))
                            .limit(placesNeeded)
                            .collect(Collectors.toList());

            resultPlaces.addAll(filteredGooglePlaces);
            filteredGooglePlaces.forEach(place -> newShownPlaceIds.add(place.getGooglePlaceId()));

            logger.info(
                    "GET NEARBY PLACES SERVICE - Added {} places from Google API (filtered {} duplicates)",
                    filteredGooglePlaces.size(),
                    googleResponse.getPlaces().size() - filteredGooglePlaces.size());

            // Generate next token with Google pagination token
            String nextToken = null;
            if (googleResponse.getNextTokenPage() != null
                    && !googleResponse.getNextTokenPage().isEmpty()) {
                // Continue with Google API pagination, database is exhausted
                nextToken =
                        PaginationTokenEncoder.encode(
                                allDbPlaces.size(),
                                newShownPlaceIds,
                                googleResponse.getNextTokenPage());
                logger.info(
                        "GET NEARBY PLACES SERVICE - Google has more results. Next token includes Google pagination token");
            } else if (resultPlaces.isEmpty()) {
                logger.info(
                        "GET NEARBY PLACES SERVICE - No more results available from either source");
            } else {
                logger.info("GET NEARBY PLACES SERVICE - All results exhausted");
            }

            logger.info(
                    "GET NEARBY PLACES SERVICE - GET NEARBY PLACES FINISH - Returning {} places total ({} from DB + {} from Google)",
                    resultPlaces.size(),
                    resultPlaces.size() - filteredGooglePlaces.size(),
                    filteredGooglePlaces.size());

            return new NearbyPlaces(resultPlaces, nextToken);
        }

        // Should never reach here, but just in case
        logger.warn("GET NEARBY PLACES SERVICE - Unexpected flow, returning empty result");
        return new NearbyPlaces(resultPlaces, null);
    }

    private Place normalizePlace(Place place) {
        return Place.builder()
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .principalPhoto(place.getPrincipalPhoto())
                .principalPhotoUrl(place.getPrincipalPhotoUrl())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .photos(place.getPhotos() != null ? place.getPhotos() : List.of())
                .build();
    }
}
