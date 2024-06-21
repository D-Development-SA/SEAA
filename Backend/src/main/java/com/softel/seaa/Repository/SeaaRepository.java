package com.softel.seaa.Repository;

import com.softel.seaa.Entity.Seaa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface SeaaRepository extends JpaRepository<Seaa, Long>, PagingAndSortingRepository<Seaa, Long> {
    Optional<Seaa> findByNameIgnoreCase(String name);
}