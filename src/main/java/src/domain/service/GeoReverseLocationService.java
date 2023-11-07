package src.domain.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.IOException;

public class GeoReverseLocationService {

    private double latitude;

    private double longitude;

    GeoReverseLocationService(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    String getCityByLocation() throws IOException, InterruptedException, ApiException {
        LatLng location = new LatLng(latitude, longitude);

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyA4qjtq1uqTddKva5LB8dGhF86xAPkXs8s")
                .build();
        GeocodingResult[] results =  GeocodingApi.reverseGeocode(context, location
        ).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents[3].longName));

        context.shutdown();
        return gson.toJson(results[0].addressComponents[3].longName);
    }

    //criar metodo reverso, obter latlong via cidade
}
