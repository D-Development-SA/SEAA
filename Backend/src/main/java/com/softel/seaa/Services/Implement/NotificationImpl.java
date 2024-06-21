package com.softel.seaa.Services.Implement;

import com.softel.seaa.Entity.Notification;
import com.softel.seaa.Repository.NotificationRepository;
import com.softel.seaa.Services.Contract.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NotificationImpl extends GenericImpl<Notification, NotificationRepository> implements NotificationService {
    @Autowired
    public NotificationImpl(NotificationRepository dao) {
        super(dao);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> findByIduserAndTypeLikeIgnoreCase(Long iduser, String type){
        return dao.findByIduserAndTypeLikeIgnoreCase(iduser, type);
    }
}
