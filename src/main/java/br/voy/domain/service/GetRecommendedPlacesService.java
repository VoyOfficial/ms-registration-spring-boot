package br.voy.domain.service;

import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.exception.PlacePageSizeExceededException;
import br.voy.domain.exception.PlaceSearchRangeExceededException;
import br.voy.domain.exception.RecommendedPlacesNotFoundException;
import br.voy.domain.ports.CurrentUserPort;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.repository.UserSavedPlaceRepository;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.domain.utils.BoundingBox;
import br.voy.domain.utils.GeoCalculator;
import br.voy.domain.utils.PaginationTokenEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GetRecommendedPlacesService implements GetRecommendedPlacesUseCase {

    @Autowired private PlaceRepository placeRepository;

    @Autowired private UserSavedPlaceRepository userSavedPlaceRepository;

    @Autowired private CurrentUserPort currentUserPort;

    @Value("${voy.services.places.initialDefaultBoundingBoxRadiusKM}")
    private double INITIAL_DEFAULT_BOUNDING_BOX_RADIUS_KM;

    @Value("${voy.services.places.incrementalBoundingBoxRadiusKM}")
    private double INCREMENTAL_BOUNDING_BOX_RADIUS_KM;

    @Value("${voy.services.places.maxPlaceSizeList}")
    private long MAX_PLACE_SIZE_LIST;

    @Value("${voy.services.places.limitMaxBoundingBox}")
    private double LIMIT_MAX_BOUNDING_BOX;

    @Value("${voy.services.places.earthRadiusKM}")
    private double EARTH_RADIUS_KM;

    @Value("${voy.services.places.maxPageSize:50}")
    private int MAX_PAGE_SIZE;

    @Override
    public List<Place> getRecommendedPlaces(
            Double userLatitude, Double userLongitude, Double range) {
        List<ScoredPlace> scoredPlaces =
                findActiveRecommendedPlaces(
                        userLatitude, userLongitude, range, (int) MAX_PLACE_SIZE_LIST);

        return scoredPlaces.stream()
                .sorted(Comparator.comparingDouble(scored -> scored.distanceKm))
                .limit(MAX_PLACE_SIZE_LIST)
                .sorted(rankingThenDistance())
                .map(scored -> scored.place)
                .collect(Collectors.toList());
    }

    @Override
    public NearbyPlaces getRecommendedPlaces(
            Double userLatitude,
            Double userLongitude,
            Double range,
            Integer pageSize,
            String nextPageToken) {
        if (pageSize != null && pageSize > MAX_PAGE_SIZE) {
            throw new PlacePageSizeExceededException(MAX_PAGE_SIZE);
        }

        int effectivePageSize =
                (pageSize != null && pageSize > 0) ? pageSize : (int) MAX_PLACE_SIZE_LIST;
        int offset = decodeOffset(nextPageToken);

        List<ScoredPlace> scoredPlaces =
                findActiveRecommendedPlaces(
                        userLatitude, userLongitude, range, offset + effectivePageSize + 1);

        List<Place> orderedPlaces =
                scoredPlaces.stream()
                        .sorted(rankingThenDistance())
                        .map(scored -> scored.place)
                        .collect(Collectors.toList());

        int totalPlaces = orderedPlaces.size();
        if (offset < 0) {
            offset = 0;
        }
        if (offset > totalPlaces) {
            return new NearbyPlaces(Collections.emptyList(), null);
        }

        int endIndex = Math.min(offset + effectivePageSize, totalPlaces);
        List<Place> paginatedPlaces = orderedPlaces.subList(offset, endIndex);

        markSavedPlaces(paginatedPlaces);

        String nextToken = endIndex < totalPlaces ? PaginationTokenEncoder.encode(endIndex) : null;
        return new NearbyPlaces(paginatedPlaces, nextToken);
    }

    private List<ScoredPlace> findActiveRecommendedPlaces(
            Double userLatitude, Double userLongitude, Double range, int minimumPlaces) {
        double radius =
                (range != null && range >= 0) ? range : INITIAL_DEFAULT_BOUNDING_BOX_RADIUS_KM;

        if (radius > LIMIT_MAX_BOUNDING_BOX) {
            throw new PlaceSearchRangeExceededException(LIMIT_MAX_BOUNDING_BOX);
        }

        List<ScoredPlace> places = new ArrayList<>();
        LocalDate today = LocalDate.now();

        do {
            BoundingBox boundingBox =
                    GeoCalculator.boundingBox(userLatitude, userLongitude, radius, EARTH_RADIUS_KM);
            Optional<List<Place>> optionalCandidates =
                    placeRepository.findPlacesWithinBoundingBox(boundingBox);

            if (optionalCandidates.isPresent() && !optionalCandidates.get().isEmpty()) {
                double currentRadius = radius;
                places =
                        optionalCandidates.get().stream()
                                .filter(place -> isActiveRecommendation(place, today))
                                .map(
                                        place ->
                                                scorePlace(
                                                        userLatitude,
                                                        userLongitude,
                                                        place,
                                                        currentRadius))
                                .filter(scored -> scored != null)
                                .collect(Collectors.toList());
            }

            radius += INCREMENTAL_BOUNDING_BOX_RADIUS_KM;
        } while (places.size() < minimumPlaces && radius <= LIMIT_MAX_BOUNDING_BOX);

        if (places.isEmpty()) {
            throw new RecommendedPlacesNotFoundException();
        }

        return places;
    }

    private ScoredPlace scorePlace(
            double userLatitude, double userLongitude, Place place, double radius) {
        double distanceKm =
                GeoCalculator.haversineKm(
                        userLatitude,
                        userLongitude,
                        place.getLatitude(),
                        place.getLongitude(),
                        EARTH_RADIUS_KM);
        if (distanceKm > radius) {
            return null;
        }
        place.setDistanceFromUserLocation(GeoCalculator.formatDistanceKm(distanceKm));
        return new ScoredPlace(place, distanceKm, place.getRanking());
    }

    private boolean isActiveRecommendation(Place place, LocalDate today) {
        if (!place.isStatus() || place.getEndRecommendation() == null) {
            return false;
        }
        if (place.getEndRecommendation().isBefore(today)) {
            return false;
        }
        return place.getStartRecommendation() == null
                || !place.getStartRecommendation().isAfter(today);
    }

    private void markSavedPlaces(List<Place> places) {
        Long currentUserId = currentUserPort.getCurrentUserId();
        if (currentUserId == null || places.isEmpty()) {
            return;
        }

        Set<Long> savedPlaceIds = userSavedPlaceRepository.findSavedPlaceIdsByUser(currentUserId);
        for (Place place : places) {
            place.setIsSaved(savedPlaceIds.contains(place.getId()));
        }
    }

    private int decodeOffset(String nextPageToken) {
        try {
            return PaginationTokenEncoder.decode(nextPageToken).getOffset();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    private Comparator<ScoredPlace> rankingThenDistance() {
        return Comparator.comparing(
                        (ScoredPlace scored) -> scored.ranking,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingDouble(scored -> scored.distanceKm);
    }

    private static final class ScoredPlace {
        private final Place place;
        private final double distanceKm;
        private final Integer ranking;

        private ScoredPlace(Place place, double distanceKm, Integer ranking) {
            this.place = place;
            this.distanceKm = distanceKm;
            this.ranking = ranking;
        }
    }
}
