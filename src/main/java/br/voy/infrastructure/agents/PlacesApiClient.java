package br.voy.infrastructure.agents;

import br.voy.domain.entity.PlacePhoto;
import br.voy.domain.exception.googlePlaces.NearbyPlaceInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.NearbyPlacesZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.OverQueryLimitApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsNotFoundApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.PlacesApiClientException;
import br.voy.domain.exception.googlePlaces.RequestDeniedApiClientException;
import br.voy.domain.exception.googlePlaces.UnknownErrorApiClientException;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.Photo;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class PlacesApiClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GeoApiContext context;

    private final String apiKey;

    public PlacesApiClient(@Value("${places.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

    }

    public PlacesSearchResponse searchForNearbyPlaces(
            LatLng latLng,
            Integer radius,
            PlaceType placeType,
            String nextPageToken
    ) {

        logger.info("PLACES API CLIENT - Create Nearby Search Request");

        var request = PlacesApi.nearbySearchQuery(context, latLng)
                .language("en")
                .radius(radius)
                .type(placeType)
                .pageToken(nextPageToken);

        try {

            logger.info("PLACES API CLIENT - Search For Nearby Places");

            return request.await();

        } catch (ZeroResultsException zeroResultsException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Zero Results Error: {}", zeroResultsException.getMessage());

            throw new NearbyPlacesZeroResultsApiClientException(zeroResultsException);

        } catch (InvalidRequestException invalidRequestException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Invalid Request Error: {}", invalidRequestException.getMessage());

            throw new NearbyPlaceInvalidRequestApiClientException(invalidRequestException);

        } catch (OverQueryLimitException overQueryLimitException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Over Query Limit Error: {}", overQueryLimitException.getMessage());

            throw new OverQueryLimitApiClientException(overQueryLimitException);

        } catch (RequestDeniedException requestDeniedException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Request Denied Error: {}", requestDeniedException.getMessage());

            throw new RequestDeniedApiClientException(requestDeniedException);

        } catch (UnknownErrorException unknownErrorException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Unknown Error: {}", unknownErrorException.getMessage());

            throw new UnknownErrorApiClientException(unknownErrorException);

        } catch (ApiException exception) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Occurred an Error: {}", exception.getMessage());

            throw new PlacesApiClientException(exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException(exception);

        }

    }

    public PlaceDetails getPlaceDetails(String placeId) {

        logger.info("PLACES API CLIENT - Create Place Details Request");

        var request = PlacesApi.placeDetails(context, placeId);

        try {

            logger.info("PLACES API CLIENT - Get Place Details");

            return request.await();

        } catch (ZeroResultsException zeroResultsException) {

            logger.warn("PLACES API CLIENT - Get Place Details - Zero Results Error: {}", zeroResultsException.getMessage());

            throw new PlaceDetailsZeroResultsApiClientException(zeroResultsException);

        } catch (NotFoundException notFoundException) {

            logger.warn("PLACES API CLIENT - Get Place Details - Not Found Error: {}", notFoundException.getMessage());

            throw new PlaceDetailsNotFoundApiClientException(notFoundException);

        } catch (InvalidRequestException invalidRequestException) {

            logger.warn("PLACES API CLIENT - Get Place Details - Invalid Request Error: {}", invalidRequestException.getMessage());

            throw new PlaceDetailsInvalidRequestApiClientException(invalidRequestException);

        } catch (OverQueryLimitException overQueryLimitException) {

            logger.warn("PLACES API CLIENT - Get Place Details - Over Query Limit Error: {}", overQueryLimitException.getMessage());

            throw new OverQueryLimitApiClientException(overQueryLimitException);

        } catch (RequestDeniedException requestDeniedException) {

            logger.warn("PLACES API CLIENT - Get Place Details - Request Denied Error: {}", requestDeniedException.getMessage());

            throw new RequestDeniedApiClientException(requestDeniedException);

        } catch (ApiException exception) {

            logger.warn("PLACES API CLIENT - Get Place Details - Occurred an Error: {}", exception.getMessage());

            throw new PlacesApiClientException(exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException(exception);

        }
    }

    public com.google.maps.model.PlaceDetails getPlaceFromText(String placeName, String city) {

        logger.info("PLACES API CLIENT - Create Find Place From Text Request to Get Place");

        // Selecting Fields what should to returned in the response of the Google Places API
        FindPlaceFromTextRequest.FieldMask[] desiredFieldsInReturn = {
//                FindPlaceFromTextRequest.FieldMask.BUSINESS_STATUS,
//                FindPlaceFromTextRequest.FieldMask.FORMATTED_ADDRESS,
//                FindPlaceFromTextRequest.FieldMask.GEOMETRY,
                FindPlaceFromTextRequest.FieldMask.NAME,
                FindPlaceFromTextRequest.FieldMask.PLACE_ID,
//                FindPlaceFromTextRequest.FieldMask.RATING,
//                FindPlaceFromTextRequest.FieldMask.OPENING_HOURS,
//                FindPlaceFromTextRequest.FieldMask.PHOTOS,
//                FindPlaceFromTextRequest.FieldMask.PRICE_LEVEL,
//                FindPlaceFromTextRequest.FieldMask.TYPES
        };

        // Getting city coordinates to refine search by getting place and finding place from text
        var cityCoordinates = getCityCoordinatesByFindPlaceFromText(city);

        var request = PlacesApi.findPlaceFromText(context, placeName, FindPlaceFromTextRequest.InputType.TEXT_QUERY)
                .fields(desiredFieldsInReturn)
                .locationBias(new FindPlaceFromTextRequest.LocationBiasCircular(cityCoordinates, 5000));

        logger.info("PLACES API CLIENT - Create Find Place From Text Request to Get Place - Sending Request to Get Place");

        try {
            var findPlaceFromTextResults = request.await();

            var placeResult = findPlaceFromTextResults.candidates.length > 0 ? findPlaceFromTextResults.candidates[0] : null;

            if (placeResult == null) {
                throw new PlacesApiClientException("An error occurred while trying to get the placeID in the response of the Find Place From Text");
            }

            if (!placeResult.name.equals(placeName)) {
                throw new PlacesApiClientException("An error occurred while trying to get the Place in the response of the Find Place From Text, Place Result Name is different from the Place Name informed");
            }

            return getPlaceDetails(placeResult.placeId);

        } catch (ZeroResultsException zeroResultsException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Zero Results Error: {}", zeroResultsException.getMessage());

            throw new PlacesApiClientException(zeroResultsException);

        } catch (InvalidRequestException invalidRequestException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Invalid Request Error: {}", invalidRequestException.getMessage());

            throw new PlacesApiClientException(invalidRequestException);

        } catch (OverQueryLimitException overQueryLimitException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Over Query Limit Error: {}", overQueryLimitException.getMessage());

            throw new OverQueryLimitApiClientException(overQueryLimitException);

        } catch (RequestDeniedException requestDeniedException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Request Denied Error: {}", requestDeniedException.getMessage());

            throw new RequestDeniedApiClientException(requestDeniedException);

        } catch (UnknownErrorException unknownErrorException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Unknown Error: {}", unknownErrorException.getMessage());

            throw new UnknownErrorApiClientException(unknownErrorException);

        } catch (ApiException exception) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Occurred an Error: {}", exception.getMessage());

            throw new PlacesApiClientException(exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException(exception);

        }
    }

    private LatLng getCityCoordinatesByFindPlaceFromText(String city) {

        logger.info("PLACES API CLIENT - Get City Coordinates By Find Place From Text");

        try {

            var cityResults = PlacesApi.findPlaceFromText(context, city, FindPlaceFromTextRequest.InputType.TEXT_QUERY)
                    .fields(FindPlaceFromTextRequest.FieldMask.GEOMETRY)
                    .await();

            LatLng cityCoordinates = cityResults.candidates.length > 0 ? cityResults.candidates[0].geometry.location : null;

            if (cityCoordinates == null) {
                throw new PlacesApiClientException("An error occurred while trying to get the coordinates of the city");
            }

            logger.info("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Returning City Coordinates");

            return cityCoordinates;

        } catch (ZeroResultsException zeroResultsException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Zero Results Error: {}", zeroResultsException.getMessage());

            throw new PlacesApiClientException(zeroResultsException);

        } catch (InvalidRequestException invalidRequestException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Invalid Request Error: {}", invalidRequestException.getMessage());

            throw new PlacesApiClientException(invalidRequestException);

        } catch (OverQueryLimitException overQueryLimitException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Over Query Limit Error: {}", overQueryLimitException.getMessage());

            throw new OverQueryLimitApiClientException(overQueryLimitException);

        } catch (RequestDeniedException requestDeniedException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Request Denied Error: {}", requestDeniedException.getMessage());

            throw new RequestDeniedApiClientException(requestDeniedException);

        } catch (UnknownErrorException unknownErrorException) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Unknown Error: {}", unknownErrorException.getMessage());

            throw new UnknownErrorApiClientException(unknownErrorException);

        } catch (ApiException exception) {

            logger.warn("PLACES API CLIENT - Get City Coordinates By Find Place From Text - Occurred an Error: {}", exception.getMessage());

            throw new PlacesApiClientException(exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException(exception);

        }
    }

    public List<PlacePhoto> getPlacePhotos(Photo[] photos) {
        List<PlacePhoto> placePhotos = new ArrayList<>();

        if (photos != null) {
            for (int i = 0; i < photos.length; i++) {
                Photo photo = photos[i];
                String photoReference = photo.photoReference;
                String photoUrl = buildPhotoUrl(photoReference, photo.width);

                try {
                    byte[] imageBytes = downloadPhoto(photoUrl); // Mantenha como byte[]
                    String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                    PlacePhoto placePhoto = new PlacePhoto();
                    placePhoto.setPhotoReference(photoReference);
                    placePhoto.setImageBase64(imageBase64); // Salve o byte[] diretamente
                    placePhoto.setHeight(photo.height);
                    placePhoto.setWidth(photo.width);
                    placePhoto.setHtmlAttributions(String.join(",", photo.htmlAttributions));

                    placePhotos.add(placePhoto);

                } catch (IOException e) {
                    System.err.println("Erro ao baixar foto: " + e.getCause());
                }
            }
        }

        return placePhotos;
    }

    private String buildPhotoUrl(String photoReference, int width) {

        int maxWidth;

        if (width > 1600) {
            maxWidth = 800; // Reduz imagens muito grandes
        } else if (width < 400) {
            maxWidth = 400; // Garante que imagens pequenas fiquem aceitáveis no celular
        } else {
            maxWidth = width; // Mantém o tamanho normal
        }

        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + maxWidth + "&photoreference=" +
               photoReference + "&key=" + apiKey;
    }

    private byte[] downloadPhoto(String photoUrl) throws IOException {
        URL url = new URL(photoUrl);
        URLConnection connection = url.openConnection();
        try (InputStream in = connection.getInputStream()) {
            return in.readAllBytes();
        }
    }

}
