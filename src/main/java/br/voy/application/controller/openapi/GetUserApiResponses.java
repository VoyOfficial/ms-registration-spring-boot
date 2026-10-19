package br.voy.application.controller.openapi;

import br.voy.application.controller.response.UserResponse;
import br.voy.domain.exception.StandardError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** OpenAPI responses for GET /v1/users/{userId}. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "200",
        description = "User Found",
        content = @Content(schema = @Schema(implementation = UserResponse.class)))
@ApiResponse(
        responseCode = "400",
        description = "Invalid user id",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "500",
        description = "Unexpected server failure",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
public @interface GetUserApiResponses {}
