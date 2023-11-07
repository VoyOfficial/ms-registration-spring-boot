package src.domain.usecase;

import com.google.maps.errors.ApiException;

import java.io.IOException;

public interface GetRecommendationPlacesUseCase {

    Object getRecommendationPlaces(double latitude, double longitude) throws IOException, InterruptedException, ApiException;
}
