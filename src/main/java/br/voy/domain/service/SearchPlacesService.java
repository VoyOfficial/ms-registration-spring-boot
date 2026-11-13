package br.voy.domain.service;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.SearchPlacesUseCase;
import br.voy.domain.utils.GeoCalculator;
import br.voy.domain.utils.PaginationTokenEncoder;
import java.util.ArrayList;
import java.util.Comparator;
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
public class SearchPlacesService implements SearchPlacesUseCase {

    private static final int DEFAULT_TEXT_SEARCH_RADIUS_METERS = 5000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${voy.services.places.nearbyPageSize:20}")
    private int pageSize;

    @Value("${voy.services.places.earthRadiusKM:6371.0}")
    private double earthRadiusKm;

    @Autowired GooglePlacesPort googlePlacesPort;

    @Autowired PlaceRepository placeRepository;

    @Autowired PlacePersistService placePersistService;

    @Override
    public NearbyPlaces searchPlaces(
            String placeName,
            Coordinates coordinates,
            List<String> types,
            String ordination,
            String nextPageToken) {

        logger.info(
                "SEARCH PLACES SERVICE - SEARCH START - place: {}, coordinates: {}, types: {}, ordination: {}, nextPageToken: {}",
                placeName,
                coordinates,
                types,
                ordination,
                nextPageToken);

        PaginationTokenEncoder.PaginationState paginationState = decodePagination(nextPageToken);
        int offset = paginationState.getOffset();
        Set<String> shownPlaceIds = new HashSet<>(paginationState.getShownPlaceIds());
        String googleToken = paginationState.getGoogleNextPageToken();

        Set<String> googleTypesFilter = PlaceSearchFilterMapper.toGoogleTypes(types);
        String normalizedOrdination = PlaceSearchOrdination.normalize(ordination);

        List<Place> matchedDbPlaces =
                placeRepository.findPlacesByNameContaining(placeName).stream()
                        .filter(place -> matchesAnyGoogleType(place, googleTypesFilter))
                        .map(place -> withDistance(place, coordinates))
                        .sorted(comparatorFor(normalizedOrdination))
                        .collect(Collectors.toList());

        List<Place> resultPlaces = new ArrayList<>();
        int consumedFromDb = 0;

        for (int i = offset; i < matchedDbPlaces.size() && resultPlaces.size() < pageSize; i++) {
            Place place = matchedDbPlaces.get(i);
            consumedFromDb++;
            if (shownPlaceIds.contains(place.getGooglePlaceId())) {
                continue;
            }
            resultPlaces.add(normalizePlace(place));
            shownPlaceIds.add(place.getGooglePlaceId());
        }

        int nextOffset = offset + consumedFromDb;
        boolean moreUnseenInDb = false;
        for (int i = nextOffset; i < matchedDbPlaces.size(); i++) {
            if (!shownPlaceIds.contains(matchedDbPlaces.get(i).getGooglePlaceId())) {
                moreUnseenInDb = true;
                break;
            }
        }

        NearbyPlaces googleResponse = null;
        if (resultPlaces.size() < pageSize) {
            String googlePlaceType =
                    googleTypesFilter.size() == 1 ? googleTypesFilter.iterator().next() : null;
            googleResponse =
                    googlePlacesPort.textSearch(
                            placeName,
                            coordinates,
                            DEFAULT_TEXT_SEARCH_RADIUS_METERS,
                            googlePlaceType,
                            googleToken);

            List<Place> googlePlaces =
                    googleResponse.getPlaces().stream()
                            .filter(place -> matchesAnyGoogleType(place, googleTypesFilter))
                            .map(place -> withDistance(place, coordinates))
                            .collect(Collectors.toList());

            if (!googlePlaces.isEmpty()) {
                persistGooglePlaces(googlePlaces);
            }

            for (Place place : googlePlaces) {
                if (resultPlaces.size() >= pageSize) {
                    break;
                }
                if (shownPlaceIds.contains(place.getGooglePlaceId())) {
                    continue;
                }
                resultPlaces.add(normalizePlace(place));
                shownPlaceIds.add(place.getGooglePlaceId());
            }

            googleToken = googleResponse.getNextTokenPage();
        }

        boolean moreUnseenGoogle =
                googleResponse != null
                        && googleResponse.getPlaces().stream()
                                .filter(place -> matchesAnyGoogleType(place, googleTypesFilter))
                                .anyMatch(
                                        place -> !shownPlaceIds.contains(place.getGooglePlaceId()));

        boolean hasGoogleNext = googleToken != null && !googleToken.isEmpty();
        boolean fullPage = resultPlaces.size() >= pageSize;
        String nextToken = null;
        if (fullPage || moreUnseenInDb || moreUnseenGoogle || hasGoogleNext) {
            nextToken =
                    PaginationTokenEncoder.encode(
                            nextOffset, shownPlaceIds, hasGoogleNext ? googleToken : null);
        }

        logger.info(
                "SEARCH PLACES SERVICE - SEARCH FINISH - results: {}, nextTokenPresent: {}",
                resultPlaces.size(),
                nextToken != null);

        return new NearbyPlaces(resultPlaces, nextToken);
    }

    private void persistGooglePlaces(List<Place> places) {
        for (Place place : places) {
            try {
                placePersistService.saveIfAbsent(place);
            } catch (Exception e) {
                logger.error(
                        "SEARCH PLACES SERVICE - Failed to persist place {}: {}",
                        place.getName(),
                        e.getMessage(),
                        e);
            }
        }
    }

    private Place withDistance(Place place, Coordinates coordinates) {
        double distanceKm =
                GeoCalculator.haversineKm(
                        coordinates.getLatitude(),
                        coordinates.getLongitude(),
                        place.getLatitude(),
                        place.getLongitude(),
                        earthRadiusKm);
        place.setDistanceFromUserLocation(GeoCalculator.formatDistanceKm(distanceKm));
        return place;
    }

    private Comparator<Place> comparatorFor(String ordination) {
        if (PlaceSearchOrdination.MOST_RATED.equals(ordination)) {
            return Comparator.comparing(
                            Place::getRating, Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(this::distanceKmValue);
        }
        if (PlaceSearchOrdination.MOST_COMMENTED.equals(ordination)) {
            return Comparator.comparing(
                            Place::getUserRatingsTotal,
                            Comparator.nullsLast(Comparator.reverseOrder()))
                    .thenComparing(this::distanceKmValue);
        }
        return Comparator.comparingDouble(this::distanceKmValue);
    }

    private double distanceKmValue(Place place) {
        String formatted = place.getDistanceFromUserLocation();
        if (formatted == null || formatted.isBlank()) {
            return Double.MAX_VALUE;
        }
        try {
            return Double.parseDouble(formatted.replace(" km", "").trim());
        } catch (NumberFormatException ex) {
            return Double.MAX_VALUE;
        }
    }

    private boolean matchesAnyGoogleType(Place place, Set<String> googleTypesFilter) {
        if (googleTypesFilter == null || googleTypesFilter.isEmpty()) {
            return true;
        }
        String types = place.getGoogleTypes();
        if (types == null || types.isBlank()) {
            return false;
        }
        for (String type : types.split(",")) {
            if (googleTypesFilter.contains(type.trim().toLowerCase())
                    || googleTypesFilter.stream()
                            .anyMatch(filter -> filter.equalsIgnoreCase(type.trim()))) {
                return true;
            }
        }
        return false;
    }

    private PaginationTokenEncoder.PaginationState decodePagination(String nextPageToken) {
        try {
            return nextPageToken != null && !nextPageToken.isEmpty()
                    ? PaginationTokenEncoder.decode(nextPageToken)
                    : new PaginationTokenEncoder.PaginationState(0, new HashSet<>(), null);
        } catch (IllegalArgumentException e) {
            logger.warn(
                    "SEARCH PLACES SERVICE - Invalid token, treating as first page: {}",
                    e.getMessage());
            return new PaginationTokenEncoder.PaginationState(0, new HashSet<>(), null);
        }
    }

    private Place normalizePlace(Place place) {
        return Place.builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .address(place.getAddress())
                .city(place.getCity())
                .principalPhoto(place.getPrincipalPhoto())
                .principalPhotoUrl(place.getPrincipalPhotoUrl())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .googleTypes(place.getGoogleTypes())
                .distanceFromUserLocation(place.getDistanceFromUserLocation())
                .photos(place.getPhotos() != null ? place.getPhotos() : List.of())
                .build();
    }
}
