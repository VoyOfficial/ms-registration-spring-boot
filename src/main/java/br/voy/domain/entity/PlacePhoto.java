package br.voy.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "PlacePhoto", description = "Photo metadata for a place")
public class PlacePhoto {

    @Schema(description = "Photo id", example = "1")
    private Long id;

    @Schema(description = "Owning place id", example = "12")
    private Long placeId;

    @Schema(description = "Provider photo reference", example = "AeJbb3e...")
    private String photoReference;

    @Schema(description = "Photo URL without API key when available")
    private String photoUrl;

    @Schema(description = "Optional base64 image payload")
    private String imageBase64;

    @Schema(description = "Image height in pixels", example = "800")
    private int height;

    @Schema(description = "Image width in pixels", example = "1200")
    private int width;

    @Schema(description = "HTML attributions from the provider")
    private String htmlAttributions;
}
