package com.home.dictionary.handler;

import com.home.dictionary.dto.errors.ErrorDetail;
import com.home.dictionary.dto.errors.ValidationError;
import com.home.dictionary.exception.ApiEntityNotFoundException;
import com.home.dictionary.exception.ApiSecurityException;
import com.home.dictionary.exception.NotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;
    private final Clock clock;

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException manve, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(System.currentTimeMillis());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("Validation Failed");
        errorDetail.setDetail("Input validation failed");
        errorDetail.setDeveloperMessage(manve.getClass().getName());

        List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();
        for (FieldError fe : fieldErrors) {

            List<ValidationError> validationErrorList = errorDetail.getErrors()
                    .computeIfAbsent(fe.getField(), k -> new ArrayList<>());
            ValidationError validationError = new ValidationError();
            validationError.setCode(fe.getCode());
            validationError.setMessage(messageSource.getMessage(fe, Locale.ENGLISH));
            validationErrorList.add(validationError);
        }
        return handleExceptionInternal(manve, errorDetail, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(System.currentTimeMillis());
        errorDetail.setStatus(status.value());
        errorDetail.setTitle("Message Not Readable");
        errorDetail.setDetail(ex.getMessage());
        errorDetail.setDeveloperMessage(ex.getClass().getName());

        return handleExceptionInternal(ex, errorDetail, headers, status, request);
    }

    @ExceptionHandler(value = ApiEntityNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(
            RuntimeException ex,
            WebRequest request
    ) {
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetail> badCredentials(BadCredentialsException badCredentialsException, HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(clock.millis());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("bad credentials");
        errorDetail.setDetail(badCredentialsException.getMessage());
        errorDetail.setDeveloperMessage(badCredentialsException.getClass().getName());
        return new ResponseEntity<>(errorDetail, null, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiSecurityException.class)
    public ResponseEntity<ErrorDetail> securityException(ApiSecurityException securityException, HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(clock.millis());
        errorDetail.setStatus(HttpStatus.FORBIDDEN.value());
        errorDetail.setTitle("forbidden");
        errorDetail.setDetail(securityException.getMessage());
        errorDetail.setDeveloperMessage(securityException.getClass().getName());
        return new ResponseEntity<>(errorDetail, null, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ErrorDetail> notAllowed(NotAllowedException notAllowedException, HttpServletRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setTimestamp(clock.millis());
        errorDetail.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetail.setTitle("Not like this Nunu! Not like this...");
        errorDetail.setDetail(notAllowedException.getMessage());
        errorDetail.setDeveloperMessage(notAllowedException.getClass().getName());
        return new ResponseEntity<>(errorDetail, null, HttpStatus.BAD_REQUEST);
    }
}
