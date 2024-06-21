package com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice;

import com.softel.seaa.Controller.Errors.Errors;
import com.softel.seaa.Controller.Exception.BDExcepcion.AccessBDException;
import com.softel.seaa.Controller.Exception.BDExcepcion.NotExistException;
import com.softel.seaa.Controller.Exception.SeaaException.DuplicateSeaaInSpecialist;
import com.softel.seaa.Controller.Exception.SeaaException.NotEspecialistException;
import com.softel.seaa.Controller.Exception.UserException.ArgumentInvalidException;
import com.softel.seaa.Controller.Exception.UserException.EmptyFieldException;
import com.softel.seaa.Controller.Exception.UserException.IncorrectFieldException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@RestControllerAdvice
public class ControllerAdvice {
    private HashMap<String, String> hashMap;
    @ExceptionHandler(value = {
            ListEmptyException.class,
            NotExistException.class,
            IncorrectFieldException.class,
            EmptyFieldException.class,
            NotEspecialistException.class,
            DuplicateSeaaInSpecialist.class
    })
    public ResponseEntity<Errors> handlerFieldInc(ICentralException ex){
        Errors e = getBuild(ex, null);

        return new ResponseEntity<>(e, ex.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handlerValidateMethod(MethodArgumentNotValidException ex){
        IncorrectFieldException incorrectFieldException;
        hashMap = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> hashMap.put(((FieldError)error).getField(), error.getDefaultMessage()));

        incorrectFieldException = new IncorrectFieldException(hashMap.size()+" incorrect fields", "???");

        Errors e = getBuild(incorrectFieldException, hashMap);

        return new ResponseEntity<>(e, incorrectFieldException.getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Errors> argumentsInvalid (MethodArgumentTypeMismatchException ex){
        ArgumentInvalidException argumentInvalidException = new ArgumentInvalidException(ex.getParameter().getParameterName(), Objects.requireNonNull(ex.getValue()).toString());
        hashMap = new HashMap<>();

        hashMap.put("Info", ex.getLocalizedMessage().split(";")[0]);
        hashMap.put("Field", ex.getName());
        hashMap.put("cause", ex.getCause().toString().split(":")[0]);
        hashMap.put("Info1", ex.getParameter().toString());
        hashMap.put("Value", Objects.requireNonNull(ex.getValue()).toString());

        Errors e = getBuild(argumentInvalidException, hashMap);

        return new ResponseEntity<>(e, argumentInvalidException.getHttpStatus());
    }

    @ExceptionHandler(value = DataAccessException.class)
    public ResponseEntity<Errors> noAccessBD(DataAccessException ex){
        hashMap = new HashMap<>();
        AccessBDException accessBDException = new AccessBDException();

        hashMap.put("error", "Cannot were perform the query to the BD correctly");
        hashMap.put("specific message", ex.getLocalizedMessage().split(";")[0]);
        hashMap.put("cause", ex.getCause().toString().split(":")[0]);
        hashMap.put("message", ex.getMessage());

        Errors e = getBuild(accessBDException, hashMap);

        return new ResponseEntity<>(e, accessBDException.getHttpStatus());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Errors> handlerUnexpected(Exception ex){
        ErrorUnexpectedException exception = new ErrorUnexpectedException();
        hashMap = new HashMap<>();

        hashMap.put("error", "Unexpected Error");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause", ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        Errors e = getBuild(exception, hashMap);

        return new ResponseEntity<>(e, exception.getHttpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<HashMap<String, String>> handlerRuntime(RuntimeException ex){
        hashMap = new HashMap<>();

        hashMap.put("error", "Unexpected runtime");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause", ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        ex.printStackTrace();

        return new ResponseEntity<>(hashMap, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<HashMap<String, String>> handlerIOException(IOException ex){
        hashMap = new HashMap<>();

        hashMap.put("error", "Read of file is failed");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause", ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        return new ResponseEntity<>(hashMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<HashMap<String, String>> handlerNullPointer(NullPointerException ex){
        hashMap = new HashMap<>();

        hashMap.put("error", "Exist something that is null");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause",ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        return new ResponseEntity<>(hashMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handlerNotFound(FileNotFoundException ex){
        hashMap = new HashMap<>();

        hashMap.put("error", "Not Found file(s)");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause",ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        return new ResponseEntity<>(hashMap, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = IndexOutOfBoundsException.class)
    public ResponseEntity<HashMap<String, String>> handlerIndexOutOfBounds(FileNotFoundException ex){
        hashMap = new HashMap<>();

        hashMap.put("error", "Index out of range");
        hashMap.put("message", ex.getMessage() != null ? ex.getMessage() : "");
        hashMap.put("specific message", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "");
        hashMap.put("cause",ex.getCause() != null && ex.getCause().getLocalizedMessage() != null ? ex.getCause().getLocalizedMessage() : "");

        return new ResponseEntity<>(hashMap, HttpStatus.NOT_FOUND);
    }

    private Errors getBuild(ICentralException centralException, HashMap<String, String> hashMap) {
        return Errors.builder()
                .code(centralException.getCode())
                .type(centralException.getType())
                .message(centralException.getMessage())
                .value(centralException.getValueField())
                .errors(hashMap)
                .build();
    }
}
