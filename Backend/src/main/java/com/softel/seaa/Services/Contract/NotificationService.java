package com.softel.seaa.Services.Contract;

import com.softel.seaa.Entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService extends GenericService<Notification>{
    Optional<Notification> findByIduserAndTypeLikeIgnoreCase(Long iduser, String type);
}
