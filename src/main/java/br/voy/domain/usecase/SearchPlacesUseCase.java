package br.voy.domain.usecase;

import br.voy.domain.entity.Coordinates;
import br.voy.domain.entity.NearbyPlaces;
import java.util.List;

public interface SearchPlacesUseCase {

    NearbyPlaces searchPlaces(
            String placeName,
            Coordinates coordinates,
            List<String> types,
            String ordination,
            String nextPageToken);
}
