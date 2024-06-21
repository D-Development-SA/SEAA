package com.softel.seaa.Controller.Exception.SeaaException;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.CentralException;
import com.softel.seaa.Entity.Seaa;
import com.softel.seaa.Entity.Specialist;
import org.springframework.http.HttpStatus;

public class DuplicateSeaaInSpecialist extends CentralException {
    public DuplicateSeaaInSpecialist(Specialist specialist, Seaa seaa) {
        super("Duplicated Seaa in the specialist -> [" + specialist.getId() + "] " +
                        "----- Have associated the SEAA -> [" + seaa.getName() + "]",
                "E-500",
                "DuplicatedSeaa",
                specialist.getId().toString(),
                HttpStatus.BAD_REQUEST);
    }
    public DuplicateSeaaInSpecialist(Specialist specialist, String seaa) {
        super("SEAA is already associated with specialist -> [" + seaa + "]",
                "E-502",
                "AlreadyAssociated",
                specialist.getId().toString(),
                HttpStatus.BAD_REQUEST);
    }
}
