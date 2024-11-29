package com.dreamgames.backendengineeringcasestudy.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiBusinessException extends RuntimeException {

    public ApiBusinessException(String message) {
        super(message);
    }

    public ApiBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}