package com.softel.seaa.Services.Thread;

import com.softel.seaa.Entity.Notification;
import com.softel.seaa.Services.Contract.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationThread {
    @Autowired
    NotificationService notificationService;

    @Async("notification")
    public CompletableFuture<Void> notificator(Notification notification){
        System.out.println("[TRYING_SAVE] :: " + notification);

        notificationService.save(notification);

        System.out.println("[NOTIFICATION] :: " + notification);

        return CompletableFuture.completedFuture(Void.TYPE.cast(new Object()));
    }

    @Async("notification")
    public CompletableFuture<Void> hiddenbtn(long id){
        Notification notification = notificationService.findByIduserAndTypeLikeIgnoreCase(id, "request").orElse(null);
        if (notification != null) {
            notification.setShowBtn(false);
            System.out.println("[TRYING_SAVE] :: " + notification);

            notificationService.save(notification);

            System.out.println("[NOTIFICATION] :: " + notification);

            notificator(Notification.builder()
                    .view(false)
                    .type("info")
                    .iduser(id)
                    .title("Nuevo especialista")
                    .content("Se ha confirmado un nuevo especialista con id -> " + id)
                    .build());

        }

        return CompletableFuture.completedFuture(Void.TYPE.cast(new Object()));
    }
}
