package src.application.interceptor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
public class StandardError {

    @Schema(example = "2022-12-18T16:43:18.114Z")
    private Instant timestamp;

    @Schema(example = "400")
    private Integer status;

    @Schema(example = "Validation")
    private String error;

    @Schema(example = "Validation Error")
    private String message;

    @Schema(example = "/api/registration/v1/users")
    private String path;

    private Map<String, String> errors = new HashMap<>();

}