package br.voy.application.controller.openapi;

import br.voy.domain.exception.StandardError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Shared Google Places client error responses for Place read endpoints. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "204",
        description = "error.places.api.zero.results.message",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "403",
        description = "error.places.api.request.denied.message",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "422",
        description = "error.places.api.invalid.request.message",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "429",
        description = "error.places.api.over.query.limit.message",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
public @interface PlacesClientErrorApiResponses {}
