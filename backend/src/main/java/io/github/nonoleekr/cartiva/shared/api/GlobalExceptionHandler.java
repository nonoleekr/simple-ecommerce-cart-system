package io.github.nonoleekr.cartiva.shared.api;

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
