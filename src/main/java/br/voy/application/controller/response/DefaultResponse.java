package br.voy.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "DefaultResponse", description = "Generic envelope used by some error payloads")
public class DefaultResponse<T> {

    @Schema(description = "Human-readable message", example = "ok")
    private String message;

    @Schema(description = "Optional payload")
    private T data;

    public DefaultResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public DefaultResponse(String message) {
        this.message = message;
    }
}
