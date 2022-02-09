package com.rpolido.springboot2essentials.handler;

import com.rpolido.springboot2essentials.exception.BadRequestException;
import com.rpolido.springboot2essentials.exception.BadRequestExceptionDetails;
import com.rpolido.springboot2essentials.exception.ExceptionDetails;
import com.rpolido.springboot2essentials.exception.ValidationExceptionDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handlerBadRequestException(BadRequestException exception) {

        return new ResponseEntity<>(BadRequestExceptionDetails.builder()
                .title("Bad Request Exception, Check the documentation")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .details(exception.getMessage())
                .developerMessage(exception.getClass().getName())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {

        final var fieldErros = exception.getBindingResult().getFieldErrors();
        final var fields = fieldErros.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        final var fieldsMessage = fieldErros.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));

        return new ResponseEntity<>(ValidationExceptionDetails.builder()
                .title("Bad Request Exception, Invalid Fields")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .details("Check the field(s) Error")
                .developerMessage(exception.getClass().getName())
                .fields(fields)
                .fieldsMessage(fieldsMessage)
                .build(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        final var exceptionDetails = ExceptionDetails.builder()
                .title(ex.getCause().getMessage())
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .details(ex.getMessage())
                .developerMessage(ex.getClass().getName())
                .build();

        return new ResponseEntity<>(exceptionDetails, headers, status);
    }
}
