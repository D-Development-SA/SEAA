package com.softel.seaa.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "seaa_data")
public class Seaa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "Name of the SEAA cannot be null")
    @NotEmpty(message = "Name of the SEAA cannot be empty")
    @Size(message = "Incorrect number of characters in the name of the SEAA", max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @NotNull(message = "Problem of the SEAA cannot be null")
    @NotEmpty(message = "Problem of the SEAA cannot be empty")
    @Size(message = "Incorrect number of characters in the problem of the SEAA", max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String problem;

    @NotNull(message = "Option of the SEAA cannot be null")
    @NotEmpty(message = "Option of the SEAA cannot be empty")
    @Size(message = "Incorrect number of characters in the option of the SEAA", max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String option;

    @Column
    private String description;

    @NotNull(message = "Insert the year of SEAA, cannot be null")
    @Column(nullable = false)
    private short year;

    @NotNull(message = "Version of the SEAA cannot be null")
    @NotEmpty(message = "Version of the SEAA cannot be empty")
    @Size(message = "Incorrect number of characters in the version of the SEAA", max = 8)
    @Column(nullable = false, length = 8)
    private String version;

    @Column(nullable = false, unique = true)
    private LocalDateTime date;

    @JsonIgnore
    @NotNull(message = "The Seaa should have a specialist")
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Query> queries = new LinkedHashSet<>();

    @PrePersist
    private void generate(){
        date = LocalDateTime.now();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Seaa seaa = (Seaa) o;
        return getId() != null && Objects.equals(getId(), seaa.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
