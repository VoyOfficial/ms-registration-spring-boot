package br.voy.application.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SearchPlacesRequest",
        description = "Optional filters and ordering for place search")
public class SearchPlacesRequest {

    @Schema(
            description = "Portuguese filter labels from the mobile app",
            example = "[\"Restaurantes\", \"Lazer\"]")
    private List<String> types;

    @Schema(
            description =
                    "Ordering criterion: Mais avaliados | Mais comentados | Mais próximo (default)",
            example = "Mais próximo")
    private String ordination;
}
