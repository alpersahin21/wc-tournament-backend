package com.dreamgames.backendengineeringcasestudy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    // Handle business logic exceptions
    @ExceptionHandler(ApiBusinessException.class)
    public ResponseEntity<ErrorResponse> handleApiBusinessException(ApiBusinessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

}
