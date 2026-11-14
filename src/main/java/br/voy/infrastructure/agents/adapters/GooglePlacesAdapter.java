package br.voy.infrastructure.agents.adapters;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.ports.GooglePlacesPort;
import br.voy.infrastructure.agents.PlacesApiClient;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "voy.services.mock.enable", havingValue = "false")
public class GooglePlacesAdapter implements GooglePlacesPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private PlacesApiClient placesApiClient;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates, Integer radius, String placeType, String nextPageToken) {

        logger.info(
                "GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Coordinates: {}, Radius: {}, PlaceType: {}",
                coordinates,
                radius,
                placeType);

        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var response =
                placesApiClient.searchForNearbyPlaces(latLng, radius, placeTypeEnum, nextPageToken);

        var places = mapResults(response.results);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Places: {}", places.size());

        return new NearbyPlaces(places, response.nextPageToken);
    }

    private Place processPlaceResult(PlacesSearchResult result) {
        Place basePlace = Place.toNearbyPlace(result);

        if (result.photos == null || result.photos.length == 0) {
            return basePlace;
        }

        var photos = placesApiClient.getPlacePhotos(new Photo[] {result.photos[0]});
        return enrichPlaceWithPhotos(basePlace, photos);
    }

    private List<Place> mapResults(PlacesSearchResult[] results) {
        if (results == null || results.length == 0) {
            return List.of();
        }
        return Arrays.stream(results)
                .parallel()
                .map(this::processPlaceResult)
                .collect(Collectors.toList());
    }

    private Place enrichPlaceWithPhotos(Place basePlace, List<PlacePhoto> photos) {
        return Place.builder()
                .googlePlaceId(basePlace.getGooglePlaceId())
                .name(basePlace.getName())
                .about(basePlace.getAbout())
                .rating(basePlace.getRating())
                .userRatingsTotal(basePlace.getUserRatingsTotal())
                .address(basePlace.getAddress())
                .principalPhoto(basePlace.getPrincipalPhoto())
                .principalPhotoUrl(basePlace.getPrincipalPhotoUrl())
                .googleTypes(basePlace.getGoogleTypes())
                .latitude(basePlace.getLatitude())
                .longitude(basePlace.getLongitude())
                .photos(photos)
                .build();
    }

    @Override
    public NearbyPlaces textSearch(
            String query,
            Coordinates coordinates,
            Integer radiusMeters,
            String placeType,
            String nextPageToken) {

        logger.info(
                "GOOGLE PLACES API ADAPTER - STARTING TEXT SEARCH - query: {}, Coordinates: {}, Radius: {}, PlaceType: {}",
                query,
                coordinates,
                radiusMeters,
                placeType);

        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var response =
                placesApiClient.textSearchQuery(
                        query, latLng, radiusMeters, placeTypeEnum, nextPageToken);

        var places = mapResults(response.results);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH TEXT SEARCH - Places: {}", places.size());

        return new NearbyPlaces(places, response.nextPageToken);
    }

    @Override
    public PlaceDetails getPlaceDetails(String placeId) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE DETAILS - Place Id: {}", placeId);

        var response = placesApiClient.getPlaceDetails(placeId);

        var photos = placesApiClient.getPlacePhotos(response.photos);

        var place = PlaceDetails.toPlaceDetailsByGoogleAndPhotos(response, photos);

        logger.info(
                "GOOGLE PLACES API ADAPTER - FINISH GET PLACE DETAILS - Place: {}",
                place.getName());

        return place;
    }

    @Override
    public PlaceDetails getPlaceFromText(String placeName, String city) {

        logger.info(
                "GOOGLE PLACES API ADAPTER - GET PLACE FROM TEXT - Place Name: {}, City: {}",
                placeName,
                city);

        var response = placesApiClient.getPlaceFromText(placeName, city);
        var photos = placesApiClient.getPlacePhotos(response.photos);
        var place = PlaceDetails.toPlaceDetailsByGoogleAndPhotos(response, photos);

        logger.info(
                "GOOGLE PLACES API ADAPTER - FINISH GET PLACE FROM TEXT - Place Response: {}",
                response);

        return place;
    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        return Optional.ofNullable(placeType)
                .filter(placeTypeString -> !placeTypeString.isEmpty())
                .map(placeTypeString -> PlaceType.valueOf(placeTypeString.toUpperCase()))
                .orElse(null);
    }
}
