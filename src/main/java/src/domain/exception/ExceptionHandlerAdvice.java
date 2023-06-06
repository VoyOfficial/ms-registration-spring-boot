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

}
