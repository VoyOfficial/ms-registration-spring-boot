package br.voy.application.advice;

import br.voy.application.controller.response.DefaultResponse;
import br.voy.domain.exception.CityDifferentPlaceRecommendationException;
import br.voy.domain.exception.InvalidUserException;
import br.voy.domain.exception.PlaceAlreadyExistsException;
import br.voy.domain.exception.StandardError;
import br.voy.domain.exception.UserNotFoundException;
import br.voy.domain.exception.googlePlaces.NearbyPlaceInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.NearbyPlacesZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.OverQueryLimitApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsInvalidRequestApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsNotFoundApiClientException;
import br.voy.domain.exception.googlePlaces.PlaceDetailsZeroResultsApiClientException;
import br.voy.domain.exception.googlePlaces.PlacesApiClientException;
import br.voy.domain.exception.googlePlaces.RequestDeniedApiClientException;
import br.voy.domain.exception.googlePlaces.UnknownErrorApiClientException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

// @RestControllerAdvice
@RequiredArgsConstructor
@ControllerAdvice
public class ExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        logger.warn("Exception Handler - Method Argument Not Valid");

        Map<String, String> errors = new HashMap<>();

        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(
                        fieldError -> {
                            String message =
                                    messageSource.getMessage(
                                            fieldError, LocaleContextHolder.getLocale());
                            errors.put(fieldError.getField(), message);
                        });

        var httpStatus = HttpStatus.BAD_REQUEST;

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Validation")
                        .message("Validation Error")
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<?> handleInvalidUserExceptionError(
            InvalidUserException exception, HttpServletRequest request) {
        logger.warn("Exception Handler - Invalid User Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Unexpected Error")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundExceptionError(
            UserNotFoundException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - User Not Found Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NOT_FOUND;

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("User Not Found")
                        .message(exception.getMessage())
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(
            NumberFormatException exception, HttpServletRequest request) {

        String path = request.getRequestURI(); // Obtém a URL completa

        if (path.startsWith("/api/registration/")) {
            path = path.substring(17); // Remove os primeiros 17 caracteres
        }

        // Normaliza qualquer ID numérico no path para "/api/registration/v1/users/{userId}"
        if (path.matches("^/v1/users/[^/]+$")) {
            path = "/v1/users/{userId}";
        }

        switch (path) {
            case "v1/places/recommendations" -> {
                logger.warn("Exception Handler - Number Format Exception");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new DefaultResponse<>(
                                        "latitude e/ou longitude no formato errado", null));
            }
            case "/v1/users/{userId}" -> {
                logger.warn("Exception Handler - Number Format Exception");

                Map<String, String> errors = Map.of("userId", "Should be a number");

                var httpStatus = HttpStatus.BAD_REQUEST;

                var message = exception.getMessage().replace("\"", "");

                var standardError =
                        StandardError.builder()
                                .timestamp(Instant.now())
                                .status(httpStatus.value())
                                .error("Invalid ID - Should be only numbers")
                                .message(message)
                                .path(request.getRequestURI())
                                .errors(errors)
                                .build();

                return ResponseEntity.status(httpStatus).body(standardError);
            }
            default -> {
                logger.warn("Exception Handler - Number Format Exception");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new DefaultResponse<>("formato errado", null));
            }
        }
    }

    @ExceptionHandler(NearbyPlacesZeroResultsApiClientException.class)
    public ResponseEntity<?> handleNearbyPlacesZeroResultsApiClientExceptionError(
            NearbyPlacesZeroResultsApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Nearby Places Zero Results Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NO_CONTENT;
        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Zero Results")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(NearbyPlaceInvalidRequestApiClientException.class)
    public ResponseEntity<?> handleNearbyPlaceInvalidRequestApiClientExceptionError(
            NearbyPlaceInvalidRequestApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Nearby Places Invalid Request Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Invalid Request")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(UnknownErrorApiClientException.class)
    public ResponseEntity<?> handleUnknownErrorApiClientExceptionError(
            UnknownErrorApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Unknown Error Api Client Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Unknown Error Google Places")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(PlaceDetailsZeroResultsApiClientException.class)
    public ResponseEntity<?> handlePlaceDetailsZeroResultsApiClientExceptionError(
            PlaceDetailsZeroResultsApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Zero Results Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NO_CONTENT;
        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Zero Results")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(PlaceDetailsNotFoundApiClientException.class)
    public ResponseEntity<?> handlePlaceNotFoundApiClientExceptionError(
            PlaceDetailsNotFoundApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Not Found Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NOT_FOUND;
        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Place Not Found")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(PlaceDetailsInvalidRequestApiClientException.class)
    public ResponseEntity<?> handlePlaceInvalidRequestApiClientExceptionError(
            PlaceDetailsInvalidRequestApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Invalid Request Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Invalid Request")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(OverQueryLimitApiClientException.class)
    public ResponseEntity<?> handleOverQueryLimitApiClientExceptionError(
            OverQueryLimitApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Over Query Limit Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Over Query Limits")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(RequestDeniedApiClientException.class)
    public ResponseEntity<?> handleORequestDeniedApiClientExceptionError(
            RequestDeniedApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Over Query Limit Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.FORBIDDEN;
        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Request Denied Google Places")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(PlacesApiClientException.class)
    public ResponseEntity<?> handlePlacesApiClientException(
            PlacesApiClientException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Places Api Client Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message = exception.getMessage().replace("\"", "");

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Internal Server Error")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(PlaceAlreadyExistsException.class)
    public ResponseEntity<?> handlePlaceAlreadyExistsExceptionError(
            PlaceAlreadyExistsException exception, HttpServletRequest request) {

        logger.warn("Exception Handler - Place Already Exists Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("Place Already Exists")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(CityDifferentPlaceRecommendationException.class)
    public ResponseEntity<?> handlePCityDifferentPlaceRecommendationExceptionError(
            CityDifferentPlaceRecommendationException exception, HttpServletRequest request) {

        logger.warn(
                "Exception Handler - City of Recommended Place is Different between Google Place");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.BAD_REQUEST;

        var message =
                messageSource.getMessage(
                        exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(httpStatus.value())
                        .error("City informed is different of city registered in Google Place")
                        .message(message)
                        .path(request.getRequestURI())
                        .errors(errors)
                        .build();

        return ResponseEntity.status(httpStatus).body(standardError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public final ResponseEntity<DefaultResponse<String>> handleResponseStatusException(
            ResponseStatusException ex) {
        // log.error(ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(new DefaultResponse<>(ex.getReason(), null));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public final ResponseEntity<DefaultResponse<String>>
            handleMissingServletRequestParameterException(
                    MissingServletRequestParameterException ex) {
        // log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DefaultResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public final ResponseEntity<DefaultResponse<String>> handleDataAccessResourceFailureException(
            DataAccessResourceFailureException ex) {
        // log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DefaultResponse<>(ex.getMessage(), null));
    }
}
