package com.softel.seaa.Services.Log;

import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Services.Contract.GenericService;

import java.util.List;

public interface LogUserServices extends GenericService<LogUser> {
    List<LogUser> findLogUsersByNameContains(String name);
    void saveLog(User user, byte method);
    void saveLog(LogUser user);
    void saveLogAll(List<User> user);
}
