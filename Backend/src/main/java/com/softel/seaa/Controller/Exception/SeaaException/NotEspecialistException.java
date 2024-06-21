package com.softel.seaa.Controller.Exception.SeaaException;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.CentralException;
import com.softel.seaa.Entity.Seaa;
import org.springframework.http.HttpStatus;

public class NotEspecialistException extends CentralException {
    public NotEspecialistException(Seaa seaa) {
        super("Specialist of " + seaa.getName() + " is null -> [Specialist]",
                "C-105",
                "NullSpecialist",
                seaa.getName(),
                HttpStatus.BAD_REQUEST);
    }
}
