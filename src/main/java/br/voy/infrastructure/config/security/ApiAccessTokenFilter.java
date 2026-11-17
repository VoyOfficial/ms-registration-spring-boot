package br.voy.infrastructure.config.security;

import br.voy.domain.exception.StandardError;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Shared-secret gate for {@code /v1/**}. Clients must send {@code Authorization: Bearer
 * <API_ACCESS_TOKEN>} when {@code voy.security.require-api-token=true}.
 */
public class ApiAccessTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final boolean requireApiToken;
    private final String configuredToken;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public ApiAccessTokenFilter(
            boolean requireApiToken,
            String configuredToken,
            ObjectMapper objectMapper,
            MessageSource messageSource) {
        this.requireApiToken = requireApiToken;
        this.configuredToken = configuredToken == null ? "" : configuredToken;
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!requireApiToken) {
            return true;
        }
        String path = request.getServletPath();
        if (path == null) {
            path = "";
        }
        return !path.startsWith("/v1/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (configuredToken.isBlank()) {
            logger.error(
                    "API access token is required but API_ACCESS_TOKEN is empty — rejecting {}",
                    request.getRequestURI());
            writeError(
                    request,
                    response,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "error.security.api.access.misconfigured",
                    "API access token is not configured");
            return;
        }

        String provided = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (provided == null || !constantTimeEquals(configuredToken, provided)) {
            logger.warn("Unauthorized API call to {}", request.getRequestURI());
            writeError(
                    request,
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "error.security.api.access.unauthorized",
                    "Missing or invalid API access token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            return null;
        }
        if (!authorizationHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private void writeError(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String messageKey,
            String fallbackMessage)
            throws IOException {
        String message =
                messageSource.getMessage(messageKey, null, fallbackMessage, Locale.getDefault());

        StandardError body =
                StandardError.builder()
                        .timestamp(Instant.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(request.getRequestURI())
                        .build();

        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
