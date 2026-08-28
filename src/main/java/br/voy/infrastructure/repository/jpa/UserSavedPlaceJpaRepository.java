package br.voy.infrastructure.repository.jpa;

import br.voy.infrastructure.model.UserSavedPlaceId;
import br.voy.infrastructure.model.UserSavedPlaceModel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSavedPlaceJpaRepository
        extends JpaRepository<UserSavedPlaceModel, UserSavedPlaceId> {

    /** Check if a user has saved a specific place */
    boolean existsByUserIdAndPlaceId(Long userId, Long placeId);

    /** Find all saved places by user ID */
    List<UserSavedPlaceModel> findAllByUserId(Long userId);

    /** Delete a saved place for a user */
    void deleteByUserIdAndPlaceId(Long userId, Long placeId);
}
