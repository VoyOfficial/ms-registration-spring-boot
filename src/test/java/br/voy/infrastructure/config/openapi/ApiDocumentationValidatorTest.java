package br.voy.infrastructure.config.openapi;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

class ApiDocumentationValidatorTest {

    private final ApiDocumentationValidator validator = new ApiDocumentationValidator();

    @Test
    @DisplayName("Current controllers and DTOs satisfy OpenAPI documentation gate")
    void currentApiSurfacePassesDocumentationGate() {
        assertThatCode(() -> validator.postProcessBeanFactory(new DefaultListableBeanFactory()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("DTO without class-level @Schema fails the gate")
    void dtoWithoutClassSchemaFails() {
        assertThatThrownBy(
                        () -> validator.validateDocumentableDto(MissingClassSchemaResponse.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("class-level @Schema");
    }

    @Test
    @DisplayName("DTO field without example or description fails the gate")
    void dtoFieldWithoutExampleOrDescriptionFails() {
        assertThatThrownBy(() -> validator.validateDocumentableDto(BlankFieldSchemaResponse.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("example or description");
    }

    @Test
    @DisplayName("Endpoint without @Operation fails the gate")
    void endpointWithoutOperationFails() throws Exception {
        Method method = UndocumentedController.class.getDeclaredMethod("list", String.class);

        assertThatThrownBy(() -> validator.validateEndpoint(UndocumentedController.class, method))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@Operation");
    }

    @Test
    @DisplayName("Endpoint without @ApiResponse fails the gate")
    void endpointWithoutApiResponseFails() throws Exception {
        Method method = MissingApiResponseController.class.getDeclaredMethod("list", String.class);

        assertThatThrownBy(
                        () ->
                                validator.validateEndpoint(
                                        MissingApiResponseController.class, method))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@ApiResponse");
    }

    @Test
    @DisplayName("Endpoint parameter without @Parameter fails the gate")
    void endpointParameterWithoutDocsFails() throws Exception {
        Method method =
                MissingParameterDocsController.class.getDeclaredMethod("list", String.class);

        assertThatThrownBy(
                        () ->
                                validator.validateEndpoint(
                                        MissingParameterDocsController.class, method))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("@Parameter");
    }

    @Schema(name = "MissingClassSchemaResponse")
    static class BlankFieldSchemaResponse {
        @Schema private String name;
    }

    static class MissingClassSchemaResponse {
        @Schema(example = "x")
        private String name;
    }

    static class UndocumentedController {
        @GetMapping("/undocumented")
        public String list(@RequestParam String q) {
            return q;
        }
    }

    static class MissingApiResponseController {
        @Operation(summary = "list")
        @GetMapping("/missing-api-response")
        public String list(@Parameter(description = "query") @RequestParam String q) {
            return q;
        }
    }

    static class MissingParameterDocsController {
        @Operation(summary = "list")
        @ApiResponse(
                responseCode = "200",
                description = "ok",
                content = @Content(schema = @Schema(implementation = String.class)))
        @GetMapping("/missing-parameter-docs")
        public String list(@RequestParam String q) {
            return q;
        }
    }
}
