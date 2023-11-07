package src.domain.service;

import com.google.maps.errors.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.usecase.GetRecommendationPlacesUseCase;

import java.io.IOException;

@Service
public class GetRecommendationPlacesService implements GetRecommendationPlacesUseCase {

    @Override
    public Object getRecommendationPlaces(double latitude, double longitude) throws IOException, InterruptedException, ApiException {

        GeoReverseLocationService geoReverseLocationService = new GeoReverseLocationService(latitude, longitude);

        return geoReverseLocationService.getCityByLocation();

    }
}
