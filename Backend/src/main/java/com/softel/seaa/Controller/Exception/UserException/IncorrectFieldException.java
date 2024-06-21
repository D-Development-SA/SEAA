package com.softel.seaa.Controller.Exception.UserException;


import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.CentralException;
import com.softel.seaa.Entity.User;
import org.springframework.http.HttpStatus;

public class IncorrectFieldException extends CentralException {
    public IncorrectFieldException(String text, String value) {
        super("Existence of a field with characters not allowed -> ["+text+"]",
                "C-101",
                "ArgumentsInvalid",
                value,
                HttpStatus.BAD_REQUEST);
    }
    public IncorrectFieldException(String text, User value) {
        super("Existence of a field with characters not allowed -> ["+text+"], \n" +
                        "if content specials characters you can solicit a exclusion",
                "C-101",
                "ArgumentsInvalid",
                value.getName(),
                HttpStatus.BAD_REQUEST);
    }
}
