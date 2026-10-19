package br.voy.application.controller.openapi;

import br.voy.domain.exception.StandardError;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@ApiResponse(
        responseCode = "201",
        description = "Resource created",
        content = @Content,
        headers = @Header(name = "Location", description = "URL of the created resource"))
@ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
@ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(schema = @Schema(implementation = StandardError.class)))
public @interface CreatedNoContentApiResponses {}
