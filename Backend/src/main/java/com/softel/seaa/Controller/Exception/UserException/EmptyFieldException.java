package com.softel.seaa.Controller.Exception.UserException;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.CentralException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmptyFieldException extends CentralException {
    public EmptyFieldException(String text, String value) {
        super("Field interpreted as empty or null -> ["+text+"]",
                "C-100",
                "NullField",
                value,
                HttpStatus.BAD_REQUEST);
    }
}
