package com.ntt.taskmanagement.common.exception;

import com.ntt.taskmanagement.common.api.ApiResponse;
import com.ntt.taskmanagement.common.api.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    // authentication & security
    @ExceptionHandler(value = ExpiredJwtException.class)
    ResponseEntity<ApiResponse<?>> handlingExpiredJwtException(ExpiredJwtException exception) {
        return buildResponse(ErrorCode.EXPIRED_JWT);
    }

    @ExceptionHandler(value = JwtException.class)
    ResponseEntity<ApiResponse<?>> handlingInvalidJwtException(JwtException exception) {
        return buildResponse(ErrorCode.INVALID_JWT);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        return buildResponse(ErrorCode.ACCESS_DENIED);
    }

    // validation & input data

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidationException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = exception.getBindingResult()
                    .getAllErrors().getFirst()
                    .unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Cannot map validation message to ErrorCode: {}", enumKey);
        }

        String message = errorCode.getMessage();

        String fieldName = exception.getFieldError().getField();
        String translatedFieldName = fieldName;

        try {
            translatedFieldName = messageSource.getMessage(fieldName, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {

        }

        message = message.replace("{field}", translatedFieldName);

        if (Objects.nonNull(attributes)) {
            message = mapAttribute(message, attributes);
        }

        return buildResponse(errorCode, message);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<?>> handlingJsonException(HttpMessageNotReadableException exception) {
        return buildResponse(ErrorCode.INVALID_JSON);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<?>> handlingTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = ErrorCode.TYPE_MISMATCH.getMessage().replace("{field}", exception.getName());
        return buildResponse(ErrorCode.TYPE_MISMATCH, message);
    }

    // custom & database
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        return buildResponse(exception.getErrorCode());
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<?>> handlingDbException(DataIntegrityViolationException exception) {
        return buildResponse(ErrorCode.DB_CONSTRAINT_VIOLATION);
    }

    // url

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<?>> handlingNotFoundException(NoResourceFoundException exception) {
        return buildResponse(ErrorCode.NOT_FOUND_URL);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        return buildResponse(ErrorCode.INVALID_METHOD_URL);
    }

    // server

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingException(Exception exception) {
        log.error("Uncategorized error occurred: ", exception);
        return buildResponse(ErrorCode.UNCATEGORIZED);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            if (key.equals("groups") || key.equals("payload") || key.equals("message")) continue;

            String value = String.valueOf(entry.getValue());
            message = message.replace("{" + key + "}", value);
        }

        // Logic replace {min}, {max} của bạn
        if (attributes.containsKey("value")) {
            String val = String.valueOf(attributes.get("value"));
            message = message.replace("{min}", val);
            message = message.replace("{max}", val);
        }
        return message;
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(message)
                        .build());
    }
}