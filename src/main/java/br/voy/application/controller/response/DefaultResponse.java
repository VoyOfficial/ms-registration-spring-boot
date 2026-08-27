package br.voy.application.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DefaultResponse<T> {
    private String message;
    private T data;

    public DefaultResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public DefaultResponse(String message) {
        this.message = message;
    }
}
