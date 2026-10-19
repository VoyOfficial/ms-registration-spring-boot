package br.voy.application.controller.openapi;

import br.voy.domain.exception.StandardError;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "204",
        description = "error.places.api.zero.results.message",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "error.places.api.request.denied.message",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "422",
        description = "error.places.api.invalid.request.message",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "429",
        description = "error.places.api.over.query.limit.message",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
public @interface PlacesClientErrorApiResponses {}
