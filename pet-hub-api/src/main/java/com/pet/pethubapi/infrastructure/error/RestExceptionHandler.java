package com.pet.pethubapi.infrastructure.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public final class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalStateException.class, IllegalAccessException.class})
    ResponseEntity<Object> handleIllegalFlowException(final RuntimeException ex, final WebRequest request) {
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /*
        Following section overrides exceptions using ProblemDetail as their response body format. Check: org.springframework.http.ProblemDetail
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode status,
                                                                     WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode status,
                                                                      WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex,
                                                                    HttpHeaders headers,
                                                                    HttpStatusCode status,
                                                                    WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatusCode status,
                                                        WebRequest request) {
        final var responseBody = processSpecialFormatException(ex, status, request);
        return handleExceptionInternal(ex, responseBody, headers, status, request);
    }

    private static ErrorResponse processSpecialFormatException(Exception ex, HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return new ErrorResponse(LocalDateTime.now(), status.value(), status.toString(), ex.getMessage(), getRequestUri(request));
    }

    private static String getRequestUri(WebRequest request) {
        final var base = ((ServletWebRequest) request).getRequest().getRequestURI();
        final var params = request.getParameterMap();

        if (CollectionUtils.isEmpty(params)) {
            return base;
        }

        final var sb = new StringBuilder();
        sb.append("?");
        params.forEach((key, value) -> sb.append(key).append("=").append(value[0]).append("&"));
        sb.deleteCharAt(sb.length() - 1);

        return base.concat(sb.toString());
    }

    private record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {

    }
    /*
        End of custom override section.
     */

}
