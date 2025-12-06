package ru.practicum.ewm.main.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ApiError error = new ApiError(
                List.of(ex.getMessage()),
                ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Failed to convert parameter '%s' with value '%s'",
                ex.getName(), ex.getValue()
        );
        ApiError error = new ApiError(
                List.of(message),
                message,
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(ErrorHandler::formatFieldError)
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                errors,
                errors.isEmpty() ? "Validation failed" : errors.get(0),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> String.format(
                        "Field: %s. Error: %s. Value: %s",
                        v.getPropertyPath(),
                        v.getMessage(),
                        v.getInvalidValue()
                ))
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                errors,
                errors.isEmpty() ? "Validation failed" : errors.get(0),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex) {
        ApiError error = new ApiError(
                List.of(ex.getMessage()),
                ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootMessage = ex.getRootCause() != null
                ? ex.getRootCause().getMessage()
                : ex.getMessage();

        ApiError error = new ApiError(
                List.of(rootMessage),
                rootMessage,
                "Integrity constraint has been violated.",
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.FORBIDDEN.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        ApiError error = new ApiError(
                List.of(),
                ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex) {
        ApiError error = new ApiError(
                List.of(ex.getMessage()),
                ex.getMessage(),
                "Unexpected error.",
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String formatFieldError(FieldError fe) {
        return String.format(
                "Field: %s. Error: %s. Value: %s",
                fe.getField(),
                fe.getDefaultMessage(),
                fe.getRejectedValue()
        );
    }
}