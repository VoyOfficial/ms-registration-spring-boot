package br.voy.infrastructure.config.openapi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Startup gate that keeps OpenAPI annotations complete for mobile contract consumers.
 *
 * <p>Covers request/response DTOs ({@code @Schema} on type and fields) and REST endpoints
 * ({@code @Operation}, {@code @ApiResponses} with schemas, {@code @Parameter} on HTTP params).
 */
@Configuration
public class ApiDocumentationValidator implements BeanFactoryPostProcessor {

    private static final String[] DTO_PACKAGES = {
        "br.voy.application.controller.request", "br.voy.application.controller.response"
    };
    private static final String CONTROLLER_PACKAGE = "br.voy.application.controller";

    private static final Class<? extends Annotation> SCHEMA =
            loadAnnotation("io.swagger.v3.oas.annotations.media.Schema");
    private static final Class<? extends Annotation> OPERATION =
            loadAnnotation("io.swagger.v3.oas.annotations.Operation");
    private static final Class<? extends Annotation> PARAMETER =
            loadAnnotation("io.swagger.v3.oas.annotations.Parameter");
    private static final Class<? extends Annotation> API_RESPONSE =
            loadAnnotation("io.swagger.v3.oas.annotations.responses.ApiResponse");
    private static final Class<? extends Annotation> CONTENT =
            loadAnnotation("io.swagger.v3.oas.annotations.media.Content");

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> loadAnnotation(String className) {
        try {
            return (Class<? extends Annotation>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Missing OpenAPI annotation type: " + className, ex);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        validateDtoAnnotations();
        validateControllerAnnotations();
    }

    private void validateDtoAnnotations() {
        Set<Class<?>> dtoClasses = new HashSet<>();
        for (String dtoPackage : DTO_PACKAGES) {
            Reflections reflections = new Reflections(dtoPackage, Scanners.SubTypes);
            dtoClasses.addAll(reflections.getSubTypesOf(Object.class));
        }

        for (Class<?> dto : dtoClasses) {
            if (!isDocumentableDto(dto)) {
                continue;
            }
            if (!dto.isAnnotationPresent(SCHEMA)) {
                throw new IllegalStateException(
                        "DTO " + dto.getName() + " must declare class-level @Schema.");
            }
            validateDtoFields(dto);
        }
    }

    private boolean isDocumentableDto(Class<?> dto) {
        if (dto.isInterface()
                || Modifier.isAbstract(dto.getModifiers())
                || dto.isAnonymousClass()) {
            return false;
        }
        String simpleName = dto.getSimpleName();
        return simpleName.endsWith("Request") || simpleName.endsWith("Response");
    }

    private void validateDtoFields(Class<?> dto) {
        for (Field field : dto.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            Annotation schema = field.getAnnotation(SCHEMA);
            if (schema == null) {
                throw new IllegalStateException(
                        "DTO field "
                                + dto.getName()
                                + "#"
                                + field.getName()
                                + " must declare @Schema with example or description.");
            }
            if (isBlank(annotationString(schema, "example"))
                    && isBlank(annotationString(schema, "description"))) {
                throw new IllegalStateException(
                        "DTO field "
                                + dto.getName()
                                + "#"
                                + field.getName()
                                + " @Schema must set example or description.");
            }
        }
    }

    private void validateControllerAnnotations() {
        Reflections reflections =
                new Reflections(CONTROLLER_PACKAGE, Scanners.TypesAnnotated, Scanners.SubTypes);
        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

        for (Class<?> controller : controllers) {
            if (!CONTROLLER_PACKAGE.equals(controller.getPackageName())) {
                continue;
            }
            for (Method method : controller.getDeclaredMethods()) {
                if (!isEndpointMethod(method)) {
                    continue;
                }
                validateOperation(controller, method);
                validateApiResponses(controller, method);
                validateParameters(controller, method);
            }
        }
    }

    private void validateOperation(Class<?> controller, Method method) {
        Annotation operation = method.getAnnotation(OPERATION);
        if (operation == null) {
            throw new IllegalStateException(
                    "Endpoint "
                            + method.getName()
                            + " in "
                            + controller.getSimpleName()
                            + " must declare @Operation.");
        }
        if (isBlank(annotationString(operation, "summary"))) {
            throw new IllegalStateException(
                    "Endpoint "
                            + method.getName()
                            + " in "
                            + controller.getSimpleName()
                            + " @Operation must set summary.");
        }
    }

    private void validateApiResponses(Class<?> controller, Method method) {
        Set<? extends Annotation> apiResponses =
                AnnotatedElementUtils.getMergedRepeatableAnnotations(method, API_RESPONSE);
        if (apiResponses.isEmpty()) {
            throw new IllegalStateException(
                    "Endpoint "
                            + method.getName()
                            + " in "
                            + controller.getSimpleName()
                            + " must declare @ApiResponse/@ApiResponses.");
        }
        for (Annotation apiResponse : apiResponses) {
            if (isBlank(annotationString(apiResponse, "description"))) {
                throw new IllegalStateException(
                        "Endpoint "
                                + method.getName()
                                + " in "
                                + controller.getSimpleName()
                                + " has @ApiResponse without description.");
            }
            if (!hasDocumentedContent(apiResponse)) {
                throw new IllegalStateException(
                        "Endpoint "
                                + method.getName()
                                + " in "
                                + controller.getSimpleName()
                                + " @ApiResponse "
                                + annotationString(apiResponse, "responseCode")
                                + " must document content schema or response examples.");
            }
        }
    }

    private boolean hasDocumentedContent(Annotation apiResponse) {
        Object contentsObj = annotationValue(apiResponse, "content");
        if (!(contentsObj instanceof Annotation[])) {
            return isNoContentStatus(annotationString(apiResponse, "responseCode"));
        }
        Annotation[] contents = (Annotation[]) contentsObj;
        if (contents.length == 0) {
            return isNoContentStatus(annotationString(apiResponse, "responseCode"));
        }
        for (Annotation content : contents) {
            if (!CONTENT.isInstance(content)) {
                continue;
            }
            Annotation schema = (Annotation) annotationValue(content, "schema");
            if (schema != null) {
                Class<?> implementation = (Class<?>) annotationValue(schema, "implementation");
                if (implementation != null
                        && implementation != Void.class
                        && implementation != void.class) {
                    return true;
                }
            }
            Object examples = annotationValue(content, "examples");
            if (examples instanceof Object[] && ((Object[]) examples).length > 0) {
                return true;
            }
            Annotation array = (Annotation) annotationValue(content, "array");
            if (array != null) {
                Annotation arraySchema = (Annotation) annotationValue(array, "schema");
                if (arraySchema != null) {
                    Class<?> implementation =
                            (Class<?>) annotationValue(arraySchema, "implementation");
                    if (implementation != null && implementation != Void.class) {
                        return true;
                    }
                }
            }
        }
        return isNoContentStatus(annotationString(apiResponse, "responseCode"));
    }

    private boolean isNoContentStatus(String responseCode) {
        return "201".equals(responseCode) || "204".equals(responseCode);
    }

    private void validateParameters(Class<?> controller, Method method) {
        for (java.lang.reflect.Parameter parameter : method.getParameters()) {
            if (!requiresParameterDocs(parameter)) {
                continue;
            }
            if (!parameter.isAnnotationPresent(PARAMETER)) {
                throw new IllegalStateException(
                        "Endpoint "
                                + method.getName()
                                + " in "
                                + controller.getSimpleName()
                                + " parameter '"
                                + parameter.getName()
                                + "' must declare @Parameter.");
            }
            Annotation docs = parameter.getAnnotation(PARAMETER);
            if (isBlank(annotationString(docs, "description"))) {
                throw new IllegalStateException(
                        "Endpoint "
                                + method.getName()
                                + " in "
                                + controller.getSimpleName()
                                + " parameter '"
                                + parameter.getName()
                                + "' @Parameter must set description.");
            }
        }
    }

    private boolean requiresParameterDocs(java.lang.reflect.Parameter parameter) {
        if (UriComponentsBuilder.class.isAssignableFrom(parameter.getType())) {
            return false;
        }
        return parameter.isAnnotationPresent(RequestParam.class)
                || parameter.isAnnotationPresent(PathVariable.class)
                || parameter.isAnnotationPresent(RequestBody.class);
    }

    private boolean isEndpointMethod(Method method) {
        return method.isAnnotationPresent(GetMapping.class)
                || method.isAnnotationPresent(PostMapping.class)
                || method.isAnnotationPresent(PutMapping.class)
                || method.isAnnotationPresent(DeleteMapping.class)
                || method.isAnnotationPresent(RequestMapping.class);
    }

    private String annotationString(Annotation annotation, String attribute) {
        Object value = annotationValue(annotation, attribute);
        return value == null ? null : String.valueOf(value);
    }

    private Object annotationValue(Annotation annotation, String attribute) {
        try {
            return annotation.annotationType().getMethod(attribute).invoke(annotation);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(
                    "Cannot read @" + annotation.annotationType().getSimpleName() + "." + attribute,
                    ex);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
