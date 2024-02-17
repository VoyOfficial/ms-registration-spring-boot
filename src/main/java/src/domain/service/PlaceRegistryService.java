package src.domain.service;

import com.google.maps.model.PlaceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.domain.entity.Place;
import src.domain.repository.PlaceRepository;
import src.domain.usecase.GetPlaceDetailsUseCase;
import src.domain.usecase.PlaceRegistryUseCase;
import src.infrastructure.agents.PlacesApiClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class PlaceRegistryService implements PlaceRegistryUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlaceRepository repository;

    @Autowired
    private GetPlaceDetailsUseCase getPlaceDetailsUseCase;

    @Autowired
    private PlacesApiClient placesApiClient;

    @Override
    public Long registry(Place placeDomain) {

        Place savedPlace = repository.savePlace(mapperPlace(placeDomain));

        logger.info("PLACE REGISTRY SERVICE - REGISTRY - Place: {}", savedPlace.getName());

        System.out.println("savedPlace: " + savedPlace);

        return savedPlace.getId();

    }

    Place mapperPlace(Place placeDomain){
        //metodo para obter os detalhes do lugar
        PlaceDetails placeDetails = placesApiClient.getPlaceFromText(placeDomain.getName(), placeDomain.getCity());

        // Define as tags de início e fim
        String startTag = "<span class=\"locality\">";
        String endTag = "</span>";

        // Encontra a posição inicial da tag de início
        int startIndex = placeDetails.adrAddress.indexOf(startTag);

        // Encontra a posição final da tag de fim, começando a busca após a tag de início
        int endIndex = placeDetails.adrAddress.indexOf(endTag, startIndex + startTag.length());

        // Extrai o conteúdo entre as tags
        String extractedCity = placeDetails.adrAddress.substring(startIndex + startTag.length(), endIndex);

        Date endRecommendation = Date.from(placeDomain.getStartRecommendation()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusMonths(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        return Place.builder()
                .googlePlaceId(placeDetails.placeId)
                .name(placeDetails.name)
                .contact(placeDetails.formattedPhoneNumber)
                .address(placeDetails.formattedAddress)
                .city(extractedCity)
                .status(placeDomain.isStatus())
                .ranking(placeDomain.getRanking())
                .startRecommendation(placeDomain.getStartRecommendation())
                .endRecommendation(endRecommendation)
                .createdDate(placeDomain.getStartRecommendation())
                .latitude(placeDetails.geometry.location.lat)
                .longitude(placeDetails.geometry.location.lng)
                .build();
    }

    public static Date converterParaDate(LocalDate localDate) {
        // Converte LocalDate para Instant e, em seguida, para Date
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
