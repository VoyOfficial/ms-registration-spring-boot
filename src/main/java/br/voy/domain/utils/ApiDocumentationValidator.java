package br.voy.domain.utils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.reflections.Reflections;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Set;

@Configuration
public class ApiDocumentationValidator implements BeanFactoryPostProcessor {

    private static final String DTO_PACKAGE = "com.seuprojeto.dtos"; // Ajuste para o seu pacote
    private static final String CONTROLLER_PACKAGE = "br.voy.application.controller"; // Ajuste para o seu pacote

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        validateDtoAnnotations();
        validateControllerAnnotations();
    }

    private void validateDtoAnnotations() {
        Reflections reflections = new Reflections(DTO_PACKAGE);
        Set<Class<?>> dtoClasses = reflections.getTypesAnnotatedWith(Schema.class);

        for (Class<?> dto : reflections.getSubTypesOf(Object.class)) {
            if (!dto.isAnnotationPresent(Schema.class)) {
                throw new IllegalStateException("❌ A classe DTO " + dto.getName() + " precisa ter @Schema.");
            }
        }
    }

    private void validateControllerAnnotations() {
        Reflections reflections = new Reflections(CONTROLLER_PACKAGE);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

        validateApiResponseAnnotations(controllers);
        validateOperationAnnotations(controllers);
        validateParameterAnnotations(controllers);
    }

    private void validateApiResponseAnnotations(Set<Class<?>> controllers) {
        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (isEndpointMethod(method) && method.getAnnotation(ApiResponses.class) == null) {
                    throw new IllegalStateException("❌ O endpoint " + method.getName() +
                                                    " no controller " + controller.getName() + " precisa ter @ApiResponses.");
                }
            }
        }
    }

    private void validateOperationAnnotations(Set<Class<?>> controllers) {
        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (isEndpointMethod(method) && method.getAnnotation(Operation.class) == null) {
                    throw new IllegalStateException("❌ O endpoint " + method.getName() +
                                                    " no controller " + controller.getName() + " precisa ter @Operation.");
                }
            }
        }
    }

    private void validateParameterAnnotations(Set<Class<?>> controllers) {
        for (Class<?> controller : controllers) {
            for (Method method : controller.getDeclaredMethods()) {
                if (isEndpointMethod(method)) {
                    boolean hasParameterAnnotations = false;
                    for (var parameter : method.getParameters()) {
                        if (parameter.isAnnotationPresent(Parameter.class)) {
                            hasParameterAnnotations = true;
                            break;
                        }
                    }
                    if (!hasParameterAnnotations) {
                        throw new IllegalStateException("❌ O endpoint " + method.getName() +
                                                        " no controller " + controller.getName() + " precisa ter @Parameter em pelo menos um parâmetro.");
                    }
                }
            }
        }
    }

    private boolean isEndpointMethod(Method method) {
        return method.isAnnotationPresent(GetMapping.class)
               || method.isAnnotationPresent(PostMapping.class)
               || method.isAnnotationPresent(PutMapping.class)
               || method.isAnnotationPresent(DeleteMapping.class)
               || method.isAnnotationPresent(RequestMapping.class);
    }
}