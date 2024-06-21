package com.softel.seaa.Services.Thread;

import com.softel.seaa.Entity.Log.LogUser;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Services.Log.LogUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LoggerThread {
    @Autowired
    private LogUserServices services;

    @Async("saveLog")
    public CompletableFuture<Void> saveLog(User user, byte method){
        services.saveLog(user, method);
        return CompletableFuture.completedFuture(Void.TYPE.cast(new Object()));
    }
    @Async("saveLog")
    public CompletableFuture<Void> saveLog(LogUser user){
        services.saveLog(user);
        return CompletableFuture.completedFuture(Void.TYPE.cast(new Object()));
    }
    @Async("saveLog")
    public CompletableFuture<Void> saveLogAll(List<User> user){
        services.saveLogAll(user);
        return CompletableFuture.completedFuture(Void.TYPE.cast(new Object()));
    }
}
