package br.voy.infrastructure.repository.relational;

import br.voy.domain.entity.Place;
import br.voy.domain.entity.UserSavedPlace;
import br.voy.domain.repository.PlaceRepository;
import br.voy.domain.repository.UserSavedPlaceRepository;
import br.voy.infrastructure.model.UserSavedPlaceModel;
import br.voy.infrastructure.repository.jpa.UserSavedPlaceJpaRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RelationalUserSavedPlaceRepository implements UserSavedPlaceRepository {

    private final UserSavedPlaceJpaRepository jpaRepository;
    private final PlaceRepository placeRepository;

    @Override
    public boolean isPlaceSavedByUser(Long userId, Long placeId) {
        if (userId == null || placeId == null) {
            return false;
        }
        return jpaRepository.existsByUserIdAndPlaceId(userId, placeId);
    }

    @Override
    public Set<Long> findSavedPlaceIdsByUser(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        return jpaRepository.findAllByUserId(userId).stream()
                .map(UserSavedPlaceModel::getPlaceId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Place> findSavedPlacesByUser(Long userId) {
        List<UserSavedPlaceModel> savedPlaces = jpaRepository.findAllByUserId(userId);

        return savedPlaces.stream()
                .map(UserSavedPlaceModel::getPlaceId)
                .map(placeRepository::findPlaceById)
                .filter(optionalPlace -> optionalPlace.isPresent())
                .map(optionalPlace -> optionalPlace.get())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void savePlaceForUser(Long userId, Long placeId) {
        if (userId == null || placeId == null) {
            throw new IllegalArgumentException("userId and placeId cannot be null");
        }

        // Check if already saved
        if (!jpaRepository.existsByUserIdAndPlaceId(userId, placeId)) {
            UserSavedPlaceModel model =
                    UserSavedPlaceModel.builder()
                            .userId(userId)
                            .placeId(placeId)
                            .savedAt(LocalDateTime.now())
                            .build();
            jpaRepository.save(model);
        }
    }

    @Override
    @Transactional
    public void removeSavedPlace(Long userId, Long placeId) {
        if (userId == null || placeId == null) {
            return;
        }
        jpaRepository.deleteByUserIdAndPlaceId(userId, placeId);
    }

    @Override
    public List<UserSavedPlace> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(UserSavedPlaceModel::toDomain)
                .collect(Collectors.toList());
    }
}
