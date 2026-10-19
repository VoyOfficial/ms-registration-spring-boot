package br.voy.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PlacePhoto {
    private Long id;
    private Long placeId;
    private String photoReference;
    private String photoUrl;
    private String imageBase64;
    private int height;
    private int width;
    private String htmlAttributions;
}
