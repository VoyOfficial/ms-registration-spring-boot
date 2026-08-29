package br.voy.domain.repository;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.UserSavedPlace;
import java.util.List;
import java.util.Set;

public interface UserSavedPlaceRepository {

    /**
     * Check if a place is saved by a specific user
     *
     * @param userId the user ID
     * @param placeId the place ID
     * @return true if the user has saved this place, false otherwise
     */
    boolean isPlaceSavedByUser(Long userId, Long placeId);

    /**
     * Get the identifiers of all places saved by a user in a single query
     *
     * @param userId the user ID
     * @return set of saved place IDs
     */
    Set<Long> findSavedPlaceIdsByUser(Long userId);

    /**
     * Get all places saved by a user
     *
     * @param userId the user ID
     * @return list of saved places
     */
    List<Place> findSavedPlacesByUser(Long userId);

    /**
     * Save a place for a user
     *
     * @param userId the user ID
     * @param placeId the place ID
     */
    void savePlaceForUser(Long userId, Long placeId);

    /**
     * Remove a saved place for a user
     *
     * @param userId the user ID
     * @param placeId the place ID
     */
    void removeSavedPlace(Long userId, Long placeId);

    /**
     * Get all UserSavedPlace records for a specific user
     *
     * @param userId the user ID
     * @return list of UserSavedPlace records
     */
    List<UserSavedPlace> findAllByUserId(Long userId);
}
