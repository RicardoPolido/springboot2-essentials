package com.rpolido.springboot2essentials.handler;

import com.rpolido.springboot2essentials.exception.BadRequestException;
import com.rpolido.springboot2essentials.exception.BadRequestExceptionDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetails> handler(BadRequestException exception) {

        return new ResponseEntity<>(BadRequestExceptionDetails.builder()
                .title("Bad Request Exception, Check the documentation")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .details(exception.getMessage())
                .developerMessage(exception.getClass().getName())
                .build(), HttpStatus.BAD_REQUEST);
    }
}
