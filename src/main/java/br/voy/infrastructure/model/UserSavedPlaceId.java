package br.voy.infrastructure.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserSavedPlaceId implements Serializable {

    private Long userId;
    private Long placeId;

}

