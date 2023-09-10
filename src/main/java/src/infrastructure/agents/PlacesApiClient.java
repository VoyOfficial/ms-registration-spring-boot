package src.infrastructure.agents;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.*;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import src.domain.exception.googlePlaces.*;

import java.io.IOException;

@Component
public class PlacesApiClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GeoApiContext context;

    public PlacesApiClient(
            @Value("${places.api.key}")
            String apiKey
    ) {

        this.context = new GeoApiContext
                .Builder()
                .apiKey(apiKey)
                .build();

    }

    public NearbySearchRequest createNearbySearchRequest(
            LatLng latLng,
            Integer radius,
            PlaceType placeType,
            String nextPageToken
    ) {

        logger.info("PLACES API CLIENT - Create Nearby Search Request");

        return PlacesApi.nearbySearchQuery(context, latLng)
                .language("en")
                .radius(radius)
                .type(placeType)
                .pageToken(nextPageToken);

    }

    public PlacesSearchResponse searchForNearbyPlaces(NearbySearchRequest request) {

        try {

            logger.info("PLACES API CLIENT - Search For Nearby Places");

            return request.await();

        } catch (ZeroResultsException zeroResultsException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Zero Results Error: {}", zeroResultsException.getMessage());

            throw new NearbyPlacesZeroResultsApiClientException(zeroResultsException);

        } catch (InvalidRequestException invalidRequestException) {

            logger.warn("PLACES API CLIENT - Search For Nearby Places - Invalid Request Error: {}", invalidRequestException.getMessage());

            throw new PlaceDetailsInvalidRequestApiClientException(invalidRequestException);

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

            throw new PlacesApiClientException("Occurred an error while getting Place Details:", exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException("Occurred an error while getting Place Details:", exception);

        }

    }

    public PlaceDetailsRequest createPlaceDetailsRequest(String placeId) {

        logger.info("PLACES API CLIENT - Create Place Details Request");

        return PlacesApi.placeDetails(context, placeId);

    }

    public PlaceDetails getPlaceDetails(PlaceDetailsRequest request) {

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

            throw new PlacesApiClientException("Occurred an error while getting Place Details:", exception);

        } catch (IOException | InterruptedException exception) {

            throw new PlacesApiClientException("Occurred an error while getting Place Details:", exception);

        }
    }

}
