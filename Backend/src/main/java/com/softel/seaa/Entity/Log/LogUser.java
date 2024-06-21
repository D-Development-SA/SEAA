package com.softel.seaa.Entity.Log;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "log_users")
@NoArgsConstructor
@AllArgsConstructor
public class LogUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private String lastName;
    @Column(nullable = false, updatable = false)
    private String ci;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    @Column(nullable = false, updatable = false)
    private byte method;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "log_user_content_id")
    private LogUserContent logUserContent;

    @PrePersist
    private void generate(){
        date = LocalDateTime.now();
    }
}
