package br.voy.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "StandardError", description = "Standard API error payload")
public class StandardError {

    @Schema(description = "Error timestamp in UTC", example = "2022-12-18T16:43:18.114Z")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "Error category", example = "Validation")
    private String error;

    @Schema(description = "Human-readable message", example = "Validation Error")
    private String message;

    @Schema(description = "Request path", example = "/api/registration/v1/users")
    private String path;

    @Schema(description = "Field-level validation errors when applicable")
    private Map<String, String> errors = new HashMap<>();
}
