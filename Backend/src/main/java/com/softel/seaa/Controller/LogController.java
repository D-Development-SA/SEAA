package com.softel.seaa.Controller;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.ListEmptyException;
import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Repository.Log.LogUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/log")
public class LogController {
    @Autowired
    LogUserRepository logUserRepository;

    @GetMapping()
    public ResponseEntity<List<LogUser>> allData() {
        List<LogUser> logUserList = logUserRepository.findAll();
        if (logUserList.isEmpty())
            throw new ListEmptyException("LogUser");

        return new ResponseEntity<>(logUserList, HttpStatus.OK);
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<LogUser> allDataUser(@PathVariable long idUser) {
        LogUser user = logUserRepository.findById(idUser).orElseThrow();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<LogUser>> allDataUserName(@PathVariable String name) {
        List<LogUser> user = logUserRepository.findLogUsersByNameContains(name);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


}
