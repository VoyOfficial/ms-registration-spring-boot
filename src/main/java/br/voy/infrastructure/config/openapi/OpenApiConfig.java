package br.voy.infrastructure.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String API_ACCESS_TOKEN_SCHEME = "bearerApiAccessToken";

    @Bean
    public GroupedOpenApi groupedOpenApi() {

        return GroupedOpenApi.builder()
                .displayName("Ms Registration")
                .group("v1")
                .packagesToScan("br.voy.application.controller")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public OpenAPI generateApiInfo() {

        var title = "MS Registration";
        var description =
                "Voy registration API for mobile integration: Users, Places (nearby, details,"
                        + " recommendations) and sponsored place registry. When"
                        + " REQUIRE_API_TOKEN=true, send Authorization: Bearer"
                        + " <API_ACCESS_TOKEN> on /v1/**.";
        var version = "0.0.1";

        var contact = new Contact().name("VoyOfficial").url("https://github.com/VoyOfficial");

        var license =
                new License()
                        .name("MIT License")
                        .url(
                                "https://github.com/VoyOfficial/ms-registration-spring-boot/blob/main/LICENSE");

        SecurityScheme bearerScheme =
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("API_ACCESS_TOKEN")
                        .description(
                                "Shared service token from API_ACCESS_TOKEN. Required when"
                                        + " REQUIRE_API_TOKEN=true.");

        return new OpenAPI()
                .info(
                        new Info()
                                .title(title)
                                .description(description)
                                .version(version)
                                .contact(contact)
                                .license(license))
                .components(
                        new Components().addSecuritySchemes(API_ACCESS_TOKEN_SCHEME, bearerScheme))
                .addSecurityItem(new SecurityRequirement().addList(API_ACCESS_TOKEN_SCHEME));
    }
}
