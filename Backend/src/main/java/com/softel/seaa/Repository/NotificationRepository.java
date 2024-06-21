package com.softel.seaa.Repository;

import com.softel.seaa.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, PagingAndSortingRepository<Notification, Long> {
    Optional<Notification> findByIduserAndTypeLikeIgnoreCase(Long iduser, String type);
}