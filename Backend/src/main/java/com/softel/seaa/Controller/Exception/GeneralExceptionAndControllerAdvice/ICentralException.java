package com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice;

import org.springframework.http.HttpStatus;

public interface ICentralException {
    String getCode();
    String getType();
    String getMessage();
    HttpStatus getHttpStatus();
    String getValueField();
}
