package br.voy.infrastructure.config.openapi;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

class ApiDocumentationValidatorTest {

    @Test
    @DisplayName("Current controllers and DTOs satisfy OpenAPI documentation gate")
    void currentApiSurfacePassesDocumentationGate() {
        ApiDocumentationValidator validator = new ApiDocumentationValidator();

        assertThatCode(() -> validator.postProcessBeanFactory(new DefaultListableBeanFactory()))
                .doesNotThrowAnyException();
    }
}
