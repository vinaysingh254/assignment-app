package com.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e,
                                                     WebRequest request) {
        return buildErrorMsg(BAD_REQUEST, e.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildErrorMsg(BAD_REQUEST, error);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
                                                                        HttpHeaders headers,
                                                                        HttpStatus status,
                                                                        WebRequest webRequest) {
        return buildErrorMsg(SERVICE_UNAVAILABLE, "Service unavailable");
    }

    private ResponseEntity<Object> buildErrorMsg(HttpStatus status,
                                                 String message) {
        return build(ApiError.builder()
                             .status(status)
                             .code(status.value())
                             .message(message)
                             .build());
    }

    private ResponseEntity<Object> build(ApiError apiError) {
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
