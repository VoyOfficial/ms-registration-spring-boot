package br.voy.application.controller.openapi;

import br.voy.application.controller.response.UserResponse;
import br.voy.domain.exception.StandardError;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "User Found",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = UserResponse.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid user id",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "User not found",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Unexpected server failure",
        content =
                @io.swagger.v3.oas.annotations.media.Content(
                        schema =
                                @io.swagger.v3.oas.annotations.media.Schema(
                                        implementation = StandardError.class)))
public @interface GetUserApiResponses {}
