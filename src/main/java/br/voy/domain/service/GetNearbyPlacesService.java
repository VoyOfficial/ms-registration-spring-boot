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

    @Value("${voy.services.places.nearbyPageSize:20}")
    private int pageSize;

    @Autowired GooglePlacesPort googlePlacesPort;

    @Autowired PlaceRepository placeRepository;

    @Autowired PlacePersistService placePersistService;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken) {

        logger.info(
                "GET NEARBY PLACES SERVICE - GET NEARBY PLACES START - Coordinates: {}, Radius: {}, PlaceType: {}, NextPageToken: {}",
                coordinates,
                radius,
                placeType,
                nextPageToken);

        PaginationTokenEncoder.PaginationState paginationState = decodePagination(nextPageToken);
        int offset = paginationState.getOffset();
        Set<String> shownPlaceIds = new HashSet<>(paginationState.getShownPlaceIds());
        String googleToken = paginationState.getGoogleNextPageToken();

        List<Place> allDbPlaces =
                placeRepository.findNearbyPlacesByCoordinates(
                        coordinates.getLatitude(), coordinates.getLongitude(), radius);

        List<Place> typedDbPlaces =
                allDbPlaces.stream()
                        .filter(place -> matchesPlaceType(place, placeType))
                        .collect(Collectors.toList());

        List<Place> resultPlaces = new ArrayList<>();
        int consumedFromDb = 0;

        for (int i = offset; i < typedDbPlaces.size() && resultPlaces.size() < pageSize; i++) {
            Place place = typedDbPlaces.get(i);
            consumedFromDb++;
            if (shownPlaceIds.contains(place.getGooglePlaceId())) {
                continue;
            }
            resultPlaces.add(normalizePlace(place));
            shownPlaceIds.add(place.getGooglePlaceId());
        }

        int nextOffset = offset + consumedFromDb;
        boolean moreUnseenInDb = false;
        for (int i = nextOffset; i < typedDbPlaces.size(); i++) {
            if (!shownPlaceIds.contains(typedDbPlaces.get(i).getGooglePlaceId())) {
                moreUnseenInDb = true;
                break;
            }
        }

        NearbyPlaces googleResponse = null;
        if (resultPlaces.size() < pageSize) {
            googleResponse =
                    googlePlacesPort.getNearbyPlaces(coordinates, radius, placeType, googleToken);

            if (!googleResponse.getPlaces().isEmpty()) {
                persistGooglePlaces(googleResponse.getPlaces(), placeType);
            }

            for (Place place : googleResponse.getPlaces()) {
                if (resultPlaces.size() >= pageSize) {
                    break;
                }
                if (shownPlaceIds.contains(place.getGooglePlaceId())) {
                    continue;
                }
                resultPlaces.add(place);
                shownPlaceIds.add(place.getGooglePlaceId());
            }

            googleToken = googleResponse.getNextTokenPage();
        }

        boolean moreUnseenGoogle =
                googleResponse != null
                        && googleResponse.getPlaces().stream()
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

        return new NearbyPlaces(resultPlaces, nextToken);
    }

    private void persistGooglePlaces(List<Place> places, String placeType) {
        for (Place place : places) {
            try {
                placePersistService.saveIfAbsent(ensurePlaceType(place, placeType));
            } catch (Exception e) {
                logger.error(
                        "GET NEARBY PLACES SERVICE - Failed to persist place {}: {}",
                        place.getName(),
                        e.getMessage(),
                        e);
            }
        }
    }

    private Place ensurePlaceType(Place place, String placeType) {
        if (placeType == null || placeType.isBlank() || matchesPlaceType(place, placeType)) {
            return place;
        }
        String existing = place.getGoogleTypes();
        String merged =
                existing == null || existing.isBlank() ? placeType : existing + "," + placeType;
        return Place.builder()
                .id(place.getId())
                .googlePlaceId(place.getGooglePlaceId())
                .name(place.getName())
                .about(place.getAbout())
                .contact(place.getContact())
                .address(place.getAddress())
                .city(place.getCity())
                .state(place.getState())
                .rating(place.getRating())
                .userRatingsTotal(place.getUserRatingsTotal())
                .principalPhoto(place.getPrincipalPhoto())
                .principalPhotoUrl(place.getPrincipalPhotoUrl())
                .status(place.isStatus())
                .ranking(place.getRanking())
                .startRecommendation(place.getStartRecommendation())
                .endRecommendation(place.getEndRecommendation())
                .createdAt(place.getCreatedAt())
                .lastCancel(place.getLastCancel())
                .distanceOfLocal(place.getDistanceOfLocal())
                .latitude(place.getLatitude())
                .longitude(place.getLongitude())
                .photos(place.getPhotos())
                .googleTypes(merged)
                .build();
    }

    private boolean matchesPlaceType(Place place, String placeType) {
        if (placeType == null || placeType.isBlank()) {
            return true;
        }
        String types = place.getGoogleTypes();
        if (types == null || types.isBlank()) {
            return false;
        }
        String needle = placeType.trim();
        for (String type : types.split(",")) {
            if (type.trim().equalsIgnoreCase(needle)) {
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
                    "GET NEARBY PLACES SERVICE - Invalid token, treating as first page: {}",
                    e.getMessage());
            return new PaginationTokenEncoder.PaginationState(0, new HashSet<>(), nextPageToken);
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
                .photos(place.getPhotos() != null ? place.getPhotos() : List.of())
                .build();
    }
}
