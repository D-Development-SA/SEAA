package com.softel.seaa.Controller;

import com.softel.seaa.Controller.Exception.GeneralExceptionAndControllerAdvice.ListEmptyException;
import com.softel.seaa.Entity.Extra.NotificationType;
import com.softel.seaa.Entity.Notification;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Security.Token;
import com.softel.seaa.Services.Contract.NotificationService;
import com.softel.seaa.Services.Contract.UserService;
import com.softel.seaa.Services.Thread.NotificationThread;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationThread notificationThread;

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<Notification>> findAllNotification() {
        List<Notification> notifications = notificationService.findAll();
        if (notifications.isEmpty()) throw new ListEmptyException("getAllUsers", "Search of all users");
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/create/requestToBeSpecialist")
    public ResponseEntity<?> requestToBeSpecialist(@CookieValue("Authorization") String token) {
        long idUser = Token.extractIdInsideToken(token);
        User user = userService.findById(idUser);
        createNotification(user.getName() + " " + user.getLastName() + " solicita ser especialista", idUser);

        return ResponseEntity.ok("");
    }

    private void createNotification(String content, long idUser) {
        Notification notification = Notification.builder()
                .view(false)
                .title("Solicitud para ser especialista")
                .content(content)
                .type(NotificationType.REQUEST)
                .showBtn(true)
                .iduser(idUser)
                .build();

        notificationThread.notificator(notification);
    }

    @PostMapping("/user/create/informationError")
    public ResponseEntity<Void> informationError(@RequestBody @Valid Notification notification) {
        notificationThread.notificator(notification);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{idNotification}")
    public ResponseEntity<?> view(@PathVariable Long idNotification) {
        Notification notification = notificationService.findById(idNotification);
        notification.setView(true);
        notificationService.save(notification);
        return ResponseEntity.ok().build();
    }
}
