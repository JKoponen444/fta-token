package com.fintechautomation.ftatoken.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fintechautomation.ftatoken.blockchain.service.BlockchainException;

/**
 * Wraps every error response in an {@link ApiResult}. Spring MVC's own exceptions (404, 405,
 * malformed JSON, ...) keep their HTTP status via {@link ResponseEntityExceptionHandler}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BlockchainException.class)
    public ResponseEntity<ApiResult> handleBlockchainException(BlockchainException ex) {
        log.warn("Blockchain request failed", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiResult.failure(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult> handleUnexpectedException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResult.failure());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiResult body = ApiResult.failure("Validation failed", fieldErrors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        if (!(body instanceof ApiResult)) {
            body = ApiResult.failure(errorMessage(body, statusCode));
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private static String errorMessage(Object body, HttpStatusCode statusCode) {
        if (body instanceof ProblemDetail problemDetail && problemDetail.getDetail() != null) {
            return problemDetail.getDetail();
        }
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null ? status.getReasonPhrase() : ApiResult.DEFAULT_FAILURE_MESSAGE;
    }
}
