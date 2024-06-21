package com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice;

import org.springframework.http.HttpStatus;

public class ListEmptyException extends CentralException {
    public ListEmptyException(String text, String value) {
        super("There is no elements in the BD with respect to petition -> ["+text+"]",
                "L-600",
                "ListEmpty",
                value,
                HttpStatus.BAD_REQUEST);
    }
    public ListEmptyException(String value) {
        super("The file not contain the tags -> ["+value+"]",
                "L-601",
                "ListEmpty, CorruptFile",
                value,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ListEmptyException(){
        super("There is no elements in the request to save",
                "L-602",
                "ListEmpty",
                "request user",
                HttpStatus.BAD_REQUEST);
    }
}
