package com.softel.seaa.Entity.Log;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "log_user_content")
@NoArgsConstructor
@AllArgsConstructor
public class LogUserContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String idFolder;

}