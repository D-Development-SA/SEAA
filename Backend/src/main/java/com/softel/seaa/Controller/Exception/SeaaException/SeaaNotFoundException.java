package com.softel.seaa.Controller.Exception.SeaaException;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.CentralException;
import org.springframework.http.HttpStatus;

public class SeaaNotFoundException extends CentralException {
    public SeaaNotFoundException(String nameSeaa) {
        super("Not found a system expert in the user or BD",
                "10",
                "SeaaNotFound",
                nameSeaa,
                HttpStatus.NOT_FOUND);
    }
}
