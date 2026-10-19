package br.voy.application.controller.openapi;

import br.voy.application.controller.response.UserResponse;
import br.voy.domain.exception.StandardError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
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
