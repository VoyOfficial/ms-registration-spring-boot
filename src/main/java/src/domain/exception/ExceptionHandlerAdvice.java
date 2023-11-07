package src.domain.exception;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import src.domain.exception.googlePlaces.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        logger.warn("Exception Handler - Method Argument Not Valid");

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
                    errors.put(fieldError.getField(), message);
                });

        var httpStatus = HttpStatus.BAD_REQUEST;

        var standardError = StandardError
                .builder()
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
            InvalidUserException exception,
            HttpServletRequest request
    ) {
        logger.warn("Exception Handler - Invalid User Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            UserNotFoundException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - User Not Found Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NOT_FOUND;

        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .error("User Not Found")
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.status(httpStatus).body(standardError);

    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(
            NumberFormatException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Number Format Exception");

        Map<String, String> errors = Map.of("userId", "Should be a number");

        var httpStatus = HttpStatus.BAD_REQUEST;

        var message = exception.getMessage().replace("\"", "");

        var standardError = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .error("Invalid ID - Should be only numbers")
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.status(httpStatus).body(standardError);

    }

    @ExceptionHandler(NearbyPlacesZeroResultsApiClientException.class)
    public ResponseEntity<?> handleNearbyPlacesZeroResultsApiClientExceptionError(
            NearbyPlacesZeroResultsApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Nearby Places Zero Results Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NO_CONTENT;
        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            NearbyPlaceInvalidRequestApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Nearby Places Invalid Request Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            UnknownErrorApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Unknown Error Api Client Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            PlaceDetailsZeroResultsApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Zero Results Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NO_CONTENT;
        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            PlaceDetailsNotFoundApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Place Not Found Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.NOT_FOUND;
        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            PlaceDetailsInvalidRequestApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Place Invalid Request Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;

        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            OverQueryLimitApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Over Query Limit Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            RequestDeniedApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Place Details Over Query Limit Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.FORBIDDEN;
        var message = messageSource.getMessage(exception.getMessage(), null, LocaleContextHolder.getLocale());

        var standardError = StandardError
                .builder()
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
            PlacesApiClientException exception,
            HttpServletRequest request) {

        logger.warn("Exception Handler - Places Api Client Exception");

        Map<String, String> errors = new HashMap<>();

        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        var message = exception.getMessage().replace("\"", "");

        var standardError = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .error("Internal Server Error")
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();

        return ResponseEntity.status(httpStatus).body(standardError);

    }

}
