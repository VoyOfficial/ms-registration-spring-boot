package br.voy.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ApiAccessTokenSecurityConfig {

    @Bean
    public FilterRegistrationBean<ApiAccessTokenFilter> apiAccessTokenFilter(
            ObjectMapper objectMapper,
            MessageSource messageSource,
            @Value("${voy.security.require-api-token:false}") boolean requireApiToken,
            @Value("${voy.security.api-access-token:}") String apiAccessToken) {

        ApiAccessTokenFilter filter =
                new ApiAccessTokenFilter(
                        requireApiToken, apiAccessToken, objectMapper, messageSource);

        FilterRegistrationBean<ApiAccessTokenFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registration.setName("apiAccessTokenFilter");
        return registration;
    }
}
