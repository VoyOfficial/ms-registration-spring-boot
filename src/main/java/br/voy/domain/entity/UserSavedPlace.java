package br.voy.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserSavedPlace {

    private Long userId;
    private Long placeId;
    private LocalDateTime savedAt;

}

