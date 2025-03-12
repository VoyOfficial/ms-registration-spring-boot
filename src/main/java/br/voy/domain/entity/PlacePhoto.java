package br.voy.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlacePhoto {
    private String photoReference;
    private String imageBase64; // Imagem em Base64
    private int height;
    private int width;
    private String htmlAttributions;
}
