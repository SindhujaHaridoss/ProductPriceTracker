package com.nhsbsa.productpricealerttracker.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhsbsa.productpricealerttracker.model.CustomErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Runtime Exception - fallback
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomErrorResponse> handleRuntimeException(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidException(InvalidDataException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, null, ex.errorCode);
    }

    // Validation errors - from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing // In case of duplicate keys
                ));

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, fieldErrors, null);
    }

    // Generic builder
    private ResponseEntity<CustomErrorResponse> buildResponse(String message, HttpStatus status,
            Map<String, String> errors, String errorCode) {
        CustomErrorResponse response = new CustomErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setMessage(message);
        response.setErrors(errors);
        response.setErrorCode(errorCode);
        return ResponseEntity.status(status).body(response);
    }
}
