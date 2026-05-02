package br.voy.infrastructure.agents.adapters;

import br.voy.infrastructure.agents.PlacesApiClient;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import br.voy.domain.entity.Place;
import br.voy.domain.entity.PlaceDetails;
import br.voy.domain.ports.GooglePlacesPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "voy.services.mock.enable", havingValue = "false")
public class GooglePlacesAdapter implements GooglePlacesPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlacesApiClient placesApiClient;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken
    ) {

        logger.info("GOOGLE PLACES API ADAPTER - STARTING NEARBY SEARCH - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
        PlaceType placeTypeEnum = createPlaceTypeEnum(placeType);

        var response = placesApiClient.searchForNearbyPlaces(latLng, radius, placeTypeEnum, nextPageToken);

        // Process all places in parallel
        var places = Arrays.stream(response.results)
                .parallel() // Enable parallel processing
                .map(this::processPlaceResult)
                .collect(Collectors.toList());

        var nearbyPlaces = new NearbyPlaces(places, response.nextPageToken);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH NEARBY SEARCH - Nearby Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

    private Place processPlaceResult(com.google.maps.model.PlacesSearchResult result) {
        Place basePlace = Place.toNearbyPlace(result, placesApiClient.getApiKey());

        // Enrich with additional photos if available
        if (result.photos != null && result.photos.length > 0) {
            var photos = placesApiClient.getPlacePhotos(result.photos);
            return enrichPlaceWithPhotos(basePlace, photos);
        }

        return basePlace;
    }

    private Place enrichPlaceWithPhotos(Place basePlace, java.util.List<br.voy.domain.entity.PlacePhoto> photos) {
        return Place.builder()
                .googlePlaceId(basePlace.getGooglePlaceId())
                .name(basePlace.getName())
                .about(basePlace.getAbout())
                .rating(basePlace.getRating())
                .userRatingsTotal(basePlace.getUserRatingsTotal())
                .address(basePlace.getAddress())
                .principalPhoto(basePlace.getPrincipalPhoto())
                .principalPhotoUrl(basePlace.getPrincipalPhotoUrl())
                .photos(photos)
                .build();
    }

    @Override
    public PlaceDetails getPlaceDetails(String placeId) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE DETAILS - Place Id: {}", placeId);

        var response = placesApiClient.getPlaceDetails(placeId);

        var photos = placesApiClient.getPlacePhotos(response.photos);

        var place = PlaceDetails.toPlaceDetailsByGoogleAndPhotos(response, photos);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH GET PLACE DETAILS - Place: {}", place.getName());

        return place;

    }

    @Override
    public PlaceDetails getPlaceFromText(String placeName, String city) {

        logger.info("GOOGLE PLACES API ADAPTER - GET PLACE FROM TEXT - Place Name: {}, City: {}", placeName, city);

        var response = placesApiClient.getPlaceFromText(placeName, city);
        var photos = placesApiClient.getPlacePhotos(response.photos);
        var place = PlaceDetails.toPlaceDetailsByGoogleAndPhotos(response, photos);

        logger.info("GOOGLE PLACES API ADAPTER - FINISH GET PLACE FROM TEXT - Place Response: {}", response);

        return place;

    }

    private PlaceType createPlaceTypeEnum(String placeType) {

        return Optional.ofNullable(placeType)
                .filter(placeTypeString -> !placeTypeString.isEmpty())
                .map(placeTypeString -> PlaceType.valueOf(placeTypeString.toUpperCase()))
                .orElse(null);

    }

}
