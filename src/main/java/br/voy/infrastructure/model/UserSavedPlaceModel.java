package br.voy.infrastructure.model;

import br.voy.domain.entity.UserSavedPlace;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_saved_places", schema = "registration")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserSavedPlaceId.class)
public class UserSavedPlaceModel {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "saved_at")
    private LocalDateTime savedAt;

    public UserSavedPlace toDomain() {
        return UserSavedPlace.builder()
                .userId(userId)
                .placeId(placeId)
                .savedAt(savedAt)
                .build();
    }

    public static UserSavedPlaceModel fromDomain(UserSavedPlace domain) {
        return UserSavedPlaceModel.builder()
                .userId(domain.getUserId())
                .placeId(domain.getPlaceId())
                .savedAt(domain.getSavedAt())
                .build();
    }

}

