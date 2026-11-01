package io.github.nonoleekr.cartiva.shared.api;

import io.github.nonoleekr.cartiva.identity.DuplicateIdentityException;
import io.github.nonoleekr.cartiva.identity.AccountUnavailableException;
import io.github.nonoleekr.cartiva.identity.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
        InvalidCredentialsException exception,
        HttpServletRequest request
    ) {
        return createResponse(
            HttpStatus.UNAUTHORIZED,
            exception.getMessage(),
            request.getRequestURI(),
            List.of()
        );
    }

    @ExceptionHandler(AccountUnavailableException.class)
    public ResponseEntity<ApiError> handleUnavailableAccount(
        AccountUnavailableException exception,
        HttpServletRequest request
    ) {
        return createResponse(
            HttpStatus.FORBIDDEN,
            exception.getMessage(),
            request.getRequestURI(),
            List.of()
        );
    }

    @ExceptionHandler(DuplicateIdentityException.class)
    public ResponseEntity<ApiError> handleDuplicateIdentity(
        DuplicateIdentityException exception,
        HttpServletRequest request
    ) {
        return createResponse(
            HttpStatus.CONFLICT,
            exception.getMessage(),
            request.getRequestURI(),
            List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationFailure(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        List<ApiError.FieldViolation> violations = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ApiError.FieldViolation(error.getField(), error.getDefaultMessage()))
            .toList();

        return createResponse(
            HttpStatus.BAD_REQUEST,
            "Request validation failed",
            request.getRequestURI(),
            violations
        );
    }

    private ResponseEntity<ApiError> createResponse(
        HttpStatus status,
        String message,
        String path,
        List<ApiError.FieldViolation> violations
    ) {
        ApiError body = new ApiError(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path,
            violations
        );
        return ResponseEntity.status(status).body(body);
    }
}
