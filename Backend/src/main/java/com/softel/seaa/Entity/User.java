package com.softel.seaa.Entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(message = "Incorrect number of characters in the name", max = 20)
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+$", message = "Exist invalid character")
    @Column(nullable = false, length = 20)
    private String name;

    @Size(message = "Incorrect number of characters", max = 100)
    @NotNull(message = "Name cannot be null")
    @NotEmpty(message = "Name cannot be empty")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?:\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)+$")
    @Column(nullable = false, length = 100)
    private String lastName;

    @NotNull(message = "It have that exist a password")
    @NotEmpty(message = "It have that exist a password")
    @Column(nullable = false, unique = true)
    private String password;

    @NotNull(message = "It have that exist a phoneNumber")
    @NotEmpty(message = "It have that exist a phoneNumber")
    @Size(message = "PhoneNumber incorrect", min = 8, max = 8)
    @Column(nullable = false, unique = true, length = 8)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    private boolean enabled;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"users_id", "roles_id"})
    )
    private Set<Rol> roles = new LinkedHashSet<>();


    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Query> queries = new LinkedHashSet<>();

    public void setQueries(Set<Query> queries) {
        this.queries.retainAll(queries);
        this.queries.addAll(queries);
    }

    @PrePersist
    private void generate(){
        date = LocalDateTime.now();
    }
    @PreUpdate
    private void encryptPassword(){
        String[] prefix = {"$2a$"," $2b$"," $2y$"," $2x$"};
        String prefixPassword = password.substring(0, 4);
        String substring = password.substring(4, 6);
        int algorithmCost = 0;

        if (password.matches("\\d+")) {
            algorithmCost = Integer.parseInt(substring);
        }
        if (password.length() != 60 &&
                Arrays.stream(prefix)
                        .noneMatch(pref -> pref.equals(prefixPassword)) &&
                !(algorithmCost > 3 && algorithmCost < 32)) {
            password = new BCryptPasswordEncoder().encode(getPassword());
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
