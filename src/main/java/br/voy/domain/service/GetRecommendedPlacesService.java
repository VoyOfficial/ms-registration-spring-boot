package br.voy.domain.service;

import br.voy.application.controller.response.PlaceResponse;
import br.voy.application.controller.response.RecommendedPlacesResponse;
import br.voy.domain.entity.Place;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.usecase.GetRecommendedPlacesUseCase;
import br.voy.domain.utils.BoundingBox;
import br.voy.domain.utils.PaginationTokenEncoder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GetRecommendedPlacesService implements GetRecommendedPlacesUseCase {

    @Autowired private PlaceRepository placeRepository;

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

    @Value("${error.places.recommendation.status400.outOfRangeRequest.message}")
    private String OUT_OF_MAX_RANGE_MESSAGE;

    @Value("${error.places.recommendation.status400.outOfRangeRequest.km}")
    private String KM;

    @Override
    public List<PlaceResponse> getRecommendedPlaces(
            Double userLatitude, Double userLongitude, Double range) {
        double radius =
                (range != null && range >= 0) ? range : INITIAL_DEFAULT_BOUNDING_BOX_RADIUS_KM;

        if (radius > LIMIT_MAX_BOUNDING_BOX) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, OUT_OF_MAX_RANGE_MESSAGE + LIMIT_MAX_BOUNDING_BOX + KM);
        }

        List<Place> places = new ArrayList<>();
        List<Place> candidates;

        // Loop para aumentar o raio caso necessário
        do {
            // 1. Calcula a bounding box para o raio atual
            BoundingBox boundingBox = calculateBoundingBox(userLatitude, userLongitude, radius);

            try {
                // 2. Busca no repositório os lugares dentro da bounding box
                Optional<List<Place>> optionalCandidates =
                        placeRepository.findPlacesWithinBoundingBox(boundingBox);
                if (optionalCandidates.isPresent() && !optionalCandidates.get().isEmpty()) {
                    candidates = optionalCandidates.get();
                    // 3. Filtra os candidatos pelo raio circular
                    places = filterByHaversine(userLatitude, userLongitude, candidates, radius);

                    // Aumenta o raio se ainda não encontrou os 5 lugares
                    radius += INCREMENTAL_BOUNDING_BOX_RADIUS_KM;
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "não encontrado");
                }
            } catch (Exception e) {
                throw e;
            }
            places.removeIf(
                    place ->
                            place.getEndRecommendation() == null
                                    || place.getEndRecommendation().isBefore(LocalDate.now()));

        } while (places.size() < MAX_PLACE_SIZE_LIST
                && radius <= LIMIT_MAX_BOUNDING_BOX); // Limita o raio máximo (ex.: 50 km)

        return orderPlacesToResponse(places, userLatitude, userLongitude);
    }

    @Override
    public RecommendedPlacesResponse getRecommendedPlaces(
            Double userLatitude,
            Double userLongitude,
            Double range,
            Integer pageSize,
            String nextPageToken) {
        // Default pageSize to MAX_PLACE_SIZE_LIST if not provided
        int effectivePageSize =
                (pageSize != null && pageSize > 0) ? pageSize : (int) MAX_PLACE_SIZE_LIST;

        // Decode offset from nextPageToken using PaginationTokenEncoder
        int offset = PaginationTokenEncoder.decode(nextPageToken);

        double radius =
                (range != null && range >= 0) ? range : INITIAL_DEFAULT_BOUNDING_BOX_RADIUS_KM;

        if (radius > LIMIT_MAX_BOUNDING_BOX) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, OUT_OF_MAX_RANGE_MESSAGE + LIMIT_MAX_BOUNDING_BOX + KM);
        }

        List<Place> allPlaces = new ArrayList<>();
        List<Place> candidates;

        // Loop para aumentar o raio caso necessário
        do {
            // 1. Calcula a bounding box para o raio atual
            BoundingBox boundingBox = calculateBoundingBox(userLatitude, userLongitude, radius);

            try {
                // 2. Busca no repositório os lugares dentro da bounding box
                Optional<List<Place>> optionalCandidates =
                        placeRepository.findPlacesWithinBoundingBox(boundingBox);
                if (optionalCandidates.isPresent() && !optionalCandidates.get().isEmpty()) {
                    candidates = optionalCandidates.get();
                    // 3. Filtra os candidatos pelo raio circular
                    allPlaces = filterByHaversine(userLatitude, userLongitude, candidates, radius);

                    // Aumenta o raio se ainda não encontrou os 5 lugares
                    radius += INCREMENTAL_BOUNDING_BOX_RADIUS_KM;
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "não encontrado");
                }
            } catch (Exception e) {
                throw e;
            }
            allPlaces.removeIf(
                    place ->
                            place.getEndRecommendation() == null
                                    || place.getEndRecommendation().isBefore(LocalDate.now()));

        } while (allPlaces.size() < (offset + effectivePageSize + 1)
                && radius <= LIMIT_MAX_BOUNDING_BOX);

        // Order all places by distance and ranking
        List<Place> orderedPlaces =
                allPlaces.stream()
                        // Ordena primeiro pela distância (mais próximos primeiro)
                        .sorted(
                                Comparator.comparingDouble(
                                        place ->
                                                calculateHaversine(
                                                        userLatitude, userLongitude, place)))
                        // Mantém apenas os 5 mais próximos
                        .sorted(
                                Comparator.comparingInt(Place::getRanking)
                                        .thenComparingDouble(
                                                place ->
                                                        calculateHaversine(
                                                                userLatitude,
                                                                userLongitude,
                                                                place)))
                        .collect(Collectors.toList());

        // Apply pagination
        int totalPlaces = orderedPlaces.size();
        int endIndex = Math.min(offset + effectivePageSize, totalPlaces);

        List<Place> paginatedPlaces = orderedPlaces.subList(offset, endIndex);

        // Generate next page token if there are more results using PaginationTokenEncoder
        String nextToken = null;
        if (endIndex < totalPlaces) {
            nextToken = PaginationTokenEncoder.encode(endIndex);
        }

        // Convert to PlaceResponse
        List<PlaceResponse> placeResponses =
                paginatedPlaces.stream()
                        .map(PlaceResponse::fromDomain)
                        .collect(Collectors.toList());

        return new RecommendedPlacesResponse(placeResponses, nextToken);
    }

    private List<PlaceResponse> orderPlacesToResponse(
            List<Place> places, Double userLatitude, Double userLongitude) {
        return places.stream()
                // Ordena primeiro pela distância (mais próximos primeiro)
                .sorted(
                        Comparator.comparingDouble(
                                place -> calculateHaversine(userLatitude, userLongitude, place)))
                // Mantém apenas os 5 mais próximos
                .limit(MAX_PLACE_SIZE_LIST)
                // Reordena agora pelo ranking, usando a distância como critério de desempate
                .sorted(
                        Comparator.comparingInt(Place::getRanking)
                                .thenComparingDouble(
                                        place ->
                                                calculateHaversine(
                                                        userLatitude, userLongitude, place)))
                // Converte para PlaceResponse
                .map(PlaceResponse::fromDomain)
                .collect(Collectors.toList());
    }

    private BoundingBox calculateBoundingBox(
            double userLatitude, double userLongitude, double radius) {
        // Converter graus para radianos
        double latRad = Math.toRadians(userLatitude);
        double lonRad = Math.toRadians(userLongitude);

        // Calcula o delta de latitude
        double deltaLat = radius / EARTH_RADIUS_KM;

        // Calcula o delta de longitude (ajustado pela latitude)
        double deltaLon = radius / (EARTH_RADIUS_KM * Math.cos(latRad));

        // Min e Max de latitude e longitude
        double minLat = Math.toDegrees(latRad - deltaLat);
        double maxLat = Math.toDegrees(latRad + deltaLat);
        double minLon = Math.toDegrees(lonRad - deltaLon);
        double maxLon = Math.toDegrees(lonRad + deltaLon);

        // Retorna a bounding box
        return new BoundingBox(minLat, maxLat, minLon, maxLon);
    }

    private List<Place> filterByHaversine(
            double userLatitude, double userLongitude, List<Place> candidates, double radius) {
        return candidates.stream()
                .filter(place -> calculateHaversine(userLatitude, userLongitude, place) <= radius)
                .collect(Collectors.toList());
    }

    private double calculateHaversine(
            double userLatitude, double userLongitude, Place candidadePlace) {
        double candidatePlaceLatitude = candidadePlace.getLatitude();
        double candidatePlaceLongitude = candidadePlace.getLongitude();

        // Converter graus para radianos
        double userLatitudeRadians = Math.toRadians(userLatitude);
        double userLongitudeRadians = Math.toRadians(userLongitude);
        double candidatePlaceLatitudeRadians = Math.toRadians(candidatePlaceLatitude);
        double candidatePlaceLongitudeRadians = Math.toRadians(candidatePlaceLongitude);

        // Diferenças de latitude e longitude
        double deltaLatitudeCandidatePlaceAndUserLocation =
                candidatePlaceLatitudeRadians - userLatitudeRadians;
        double deltaLongitudeCandidatePlaceAndUserLocation =
                candidatePlaceLongitudeRadians - userLongitudeRadians;

        // Fórmula de Haversine
        double a =
                Math.pow(Math.sin(deltaLatitudeCandidatePlaceAndUserLocation / 2), 2)
                        + Math.cos(userLatitudeRadians)
                                * Math.cos(candidatePlaceLatitudeRadians)
                                * Math.pow(
                                        Math.sin(deltaLongitudeCandidatePlaceAndUserLocation / 2),
                                        2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distância em km
        double distance = EARTH_RADIUS_KM * c;
        BigDecimal distanceRounded = BigDecimal.valueOf(distance).setScale(3, RoundingMode.HALF_UP);

        candidadePlace.setDistanceFromUserLocation(distanceRounded + " km");
        return distance;
    }
}
