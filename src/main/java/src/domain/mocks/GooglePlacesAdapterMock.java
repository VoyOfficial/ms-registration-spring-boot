package src.domain.mocks;

import com.google.maps.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import src.domain.entity.Coordinates;
import src.domain.entity.NearbyPlaces;
import src.domain.entity.Place;
import src.domain.mapper.PlaceDetailsMapper;
import src.domain.ports.GooglePlacesPort;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "services.mock.enable", havingValue = "true")
public class GooglePlacesAdapterMock implements GooglePlacesPort {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceDetailsMapper placeDetailsMapper;

    @Override
    public NearbyPlaces getNearbyPlaces(
            Coordinates coordinates,
            Integer radius,
            String placeType,
            String nextPageToken) {

        logger.info("GOOGLE PLACES API ADAPTER MOCK - STARTING NEARBY SEARCH MOCKED - Coordinates: {}, Radius: {}, PlaceType: {}", coordinates, radius, placeType);

        PlacesSearchResponse placesSearchResponse = new PlacesSearchResponse();

        PlacesSearchResult result1 = new PlacesSearchResult();
        result1.name = "Restaurante Fictício";
        result1.formattedAddress = "123 Rua Fictícia, Cidade Fictícia";
        result1.placeId = "place_id_123";
        result1.types = new String[]{"restaurant"};
        result1.rating = 5.0f;
        result1.userRatingsTotal = 5;
        result1.geometry = new Geometry();
        result1.geometry.location = new LatLng(37.12345, -122.67890);

        Photo photo1 = new Photo();
        photo1.photoReference = "photo_reference_1";

        result1.photos = new Photo[]{photo1};

        PlacesSearchResult result2 = new PlacesSearchResult();
        result2.name = "Hotel Fictício";
        result2.formattedAddress = "456 Avenida Fictícia, Cidade Fictícia";
        result2.placeId = "place_id_456";
        result2.types = new String[]{"lodging"};
        result2.rating = 3.0f;
        result2.userRatingsTotal = 10;
        result2.geometry = new Geometry();
        result2.geometry.location = new LatLng(37.54321, -122.98765);

        Photo photo2 = new Photo();
        photo2.photoReference = "photo_reference_2";
        result2.photos = new Photo[]{photo2};

        PlacesSearchResult result3 = new PlacesSearchResult();
        result3.name = "Parque Fictício";
        result3.formattedAddress = "789 Praça Fictícia, Cidade Fictícia";
        result3.placeId = "place_id_789";
        result3.types = new String[]{"park"};
        result3.rating = 4.5f;
        result3.userRatingsTotal = 50;
        result3.geometry = new Geometry();
        result3.geometry.location = new LatLng(37.98765, -122.12345);

        Photo photo3 = new Photo();
        photo3.photoReference = "photo_reference_3";
        result3.photos = new Photo[]{photo3};

        PlacesSearchResult result4 = new PlacesSearchResult();
        result4.name = "Museu Fictício";
        result4.formattedAddress = "101 Museu Fictício, Cidade Fictícia";
        result4.placeId = "place_id_101";
        result4.types = new String[]{"museum"};
        result4.rating = 4.8f;
        result4.userRatingsTotal = 100;
        result4.geometry = new Geometry();
        result4.geometry.location = new LatLng(37.65432, -122.23456);

        Photo photo4 = new Photo();
        photo4.photoReference = "photo_reference_4";
        result4.photos = new Photo[]{photo4};

        placesSearchResponse.results = new PlacesSearchResult[]{result1, result2, result3, result4};
        placesSearchResponse.nextPageToken = "3ee92b07-1305-4d1c-b5bb-b9283e9b1337";

        var places = Arrays.stream(placesSearchResponse.results)
                .map(Place::toNearbyPlace)
                .collect(Collectors.toList());

        var nearbyPlaces = new NearbyPlaces(places, placesSearchResponse.nextPageToken);

        logger.info("GOOGLE PLACES API ADAPTER MOCK - FINISH NEARBY SEARCH MOCKED - Nearby Places: {}", nearbyPlaces);

        return nearbyPlaces;

    }

    @Override
    public src.domain.entity.PlaceDetails getPlaceDetails(String placeId) {

        PlaceDetails details = new PlaceDetails();

        details.addressComponents = new AddressComponent[]{new AddressComponent()};
        details.adrAddress = "Adr Address";
        details.businessStatus = "OPERATIONAL";
        details.curbsidePickup = true;
        details.currentOpeningHours = new OpeningHours();
        details.delivery = true;
        details.dineIn = true;
        details.editorialSummary = new PlaceEditorialSummary();
        details.formattedAddress = "Formatted Address";
        details.formattedPhoneNumber = "123-456-7890";
        details.geometry = new Geometry();
        details.internationalPhoneNumber = "+1 123-456-7890";
        details.name = "Random Place";
        details.openingHours = new OpeningHours();
        details.photos = new Photo[]{new Photo()};
        details.placeId = "random_place_id";
        details.plusCode = new PlusCode();
        details.priceLevel = PriceLevel.MODERATE;
        details.rating = 4.5f;
        details.reservable = true;
        details.reviews = new PlaceDetails.Review[]{new PlaceDetails.Review()};
        details.secondaryOpeningHours = new OpeningHours();
        details.servesBeer = true;
        details.servesBreakfast = true;
        details.servesBrunch = true;
        details.servesDinner = true;
        details.servesLunch = true;
        details.servesVegetarianFood = true;
        details.servesWine = true;
        details.takeout = true;
        details.types = new AddressType[]{AddressType.LODGING, AddressType.POINT_OF_INTEREST, AddressType.ESTABLISHMENT};
        details.userRatingsTotal = 100;
        details.utcOffset = -180;
        details.vicinity = "Random Vicinity";
        details.wheelchairAccessibleEntrance = true;
        details.htmlAttributions = new String[]{"attribution1", "attribution2"};

        return placeDetailsMapper.toPlaceDetailsByGoogle(details);

    }

    @Override
    public PlaceDetails getPlaceFromText(String placeName, String city) {
        return null; // TODO implementar mock
    }


}
