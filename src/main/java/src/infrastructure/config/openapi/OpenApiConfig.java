package src.infrastructure.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi groupedOpenApi() {

        return GroupedOpenApi
                .builder()
                .displayName("Ms Registration")
                .group("v1")
                .packagesToScan("src.application.controller")
                .build();

    }

    @Bean
    public OpenAPI generateApiInfo() {

        var title = "MS Registration";
        var description = "API for to perfom User CRUD operations";
        var version = "0.0.1";

        var contact = new Contact()
                .name("VoyOfficial")
                .url("https://github.com/VoyOfficial");

        var license = new License()
                .name("MIT License")
                .url("https://github.com/VoyOfficial/ms-registration-spring-boot/blob/main/LICENSE");

        return new OpenAPI().info(
                new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(contact)
                        .license(license)

        );

    }

}
