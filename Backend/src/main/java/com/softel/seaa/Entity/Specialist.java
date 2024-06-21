package com.softel.seaa.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "specialists")
public class Specialist implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Pattern(message = "CI can only contain numbers", regexp = "[0-9]*")
    @Column(unique = true, length = 11)
    private String ci;
    @Pattern(message = "KnowledgeArea can only contain letters", regexp = "[a-zA-Z]*")
    @Size(message = "KnowledgeArea incorrect, should have maximum 100 characters", max = 100)
    @Column(length = 100)
    private String knowledgeArea;
    @Size(message = "ScientificCategory incorrect, should have maximum 100 characters", max = 100)
    @Column(length = 100)
    private String scientificCategory;
    @Size(message = "ProfessionalRegister incorrect, should have maximum 100 characters", max = 100)
    @Column(length = 100)
    private String professionalRegister;
    @Size(message = "ProfessionalRegister incorrect, should have maximum 500 characters", max = 500)
    @Column(length = 500)
    private String biography;

    @OneToMany(mappedBy = "specialist", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private Set<Seaa> seaaList = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> seaaShared = new LinkedHashSet<>();

    @PrePersist
    private void validateCI(){
        if (ci != null && !ci.isEmpty() && ci.length() != 11) {
            throw new IllegalArgumentException("CI incorrect, should have 11 characters");
        }
    }

}