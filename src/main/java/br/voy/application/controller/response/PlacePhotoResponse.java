package br.voy.application.controller.response;

import br.voy.domain.entity.PlacePhoto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PlacePhotoResponse", description = "Photo metadata for a place")
public class PlacePhotoResponse {

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

    public static PlacePhotoResponse fromDomain(PlacePhoto photo, String photoUrl) {
        return PlacePhotoResponse.builder()
                .id(photo.getId())
                .placeId(photo.getPlaceId())
                .photoReference(photo.getPhotoReference())
                .photoUrl(photoUrl)
                .imageBase64(photo.getImageBase64())
                .height(photo.getHeight())
                .width(photo.getWidth())
                .htmlAttributions(photo.getHtmlAttributions())
                .build();
    }
}
