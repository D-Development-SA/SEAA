package com.softel.seaa.Entity;

import com.softel.seaa.Entity.Extra.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private Long iduser;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private boolean view;
    @Column(nullable = false, unique = true)
    private LocalDateTime date;
    @Column(nullable = false)
    private boolean showBtn = false;

    @PrePersist()
    private void generate(){
        date = LocalDateTime.now();
    }
}